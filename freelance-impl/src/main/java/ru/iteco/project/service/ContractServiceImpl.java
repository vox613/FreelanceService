package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.*;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidContractStatusException;
import ru.iteco.project.repository.*;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoResponse;
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.service.jms.BookKeepingService;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.math.BigDecimal;
import java.util.*;

import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.*;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.DONE;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.*;
import static ru.iteco.project.service.specifications.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {
    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());

    @Value("${errors.contract.status.invalid}")
    private String invalidContractStatusMessage;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект сервисного слоя заданий */
    private final UserService userService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Contract> specificationBuilder;

    /*** Объект маппера dto <-> сущность договога */
    private final MapperFacade mapperFacade;

    private final BookKeepingService bookKeepingService;


    public ContractServiceImpl(ContractRepository contractRepository, ContractStatusRepository contractStatusRepository,
                               TaskService taskService, UserService userService, SpecificationBuilder<Contract> specificationBuilder,
                               MapperFacade mapperFacade, BookKeepingService bookKeepingService) {
        this.contractRepository = contractRepository;
        this.contractStatusRepository = contractStatusRepository;
        this.taskService = taskService;
        this.userService = userService;
        this.specificationBuilder = specificationBuilder;
        this.mapperFacade = mapperFacade;
        this.bookKeepingService = bookKeepingService;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContractDtoResponse> getAllContracts() {
        ArrayList<ContractDtoResponse> contractDtoResponses = new ArrayList<>();
        for (Contract contract : contractRepository.findAll()) {
            contractDtoResponses.add(enrichContractInfo(contract));
        }
        return contractDtoResponses;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
//    @Transactional(readOnly = true)
    public ContractDtoResponse getContractById(UUID id) {
        Contract contract = getContractEntityById(id);
        ContractDtoResponse contractDtoResponse = enrichContractInfo(contract);
        bookKeepingService.sendReportToBookKeeping(contract);
        return contractDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractDtoResponse createContract(ContractDtoRequest contractDtoRequest) {
        ContractDtoResponse contractDtoResponse = null;

        Task task = taskService.getTaskEntityById(contractDtoRequest.getTaskId());
        User executor = userService.getUserEntityById(contractDtoRequest.getExecutorId());

        if (isAllowToCreateContract(task, executor, contractDtoRequest)) {
            Contract contract = new Contract();
            contract.setId(UUID.randomUUID());
            if (!UserStatus.UserStatusEnum.isEqualsUserStatus(ACTIVE, executor)) {
                executor.setUserStatus(userService.getUserStatusByValue(ACTIVE.name()));
            }
            contract.setCustomer(task.getCustomer());
            contract.setExecutor(executor);
            task.setTaskStatus(taskService.getTaskStatusByValue(IN_PROGRESS.name()));
            task.setExecutor(executor);
            contract.setTask(task);
            contract.setContractStatus(getContractStatusByValue(PAID.name()));

            User taskCustomer = contract.getCustomer();
            taskCustomer.setWallet(taskCustomer.getWallet().subtract(task.getPrice()));
            Contract save = contractRepository.save(contract);
            contractDtoResponse = enrichContractInfo(save);
        }
        return contractDtoResponse;
    }

    private boolean isAllowToCreateContract(Task task, User executor, ContractDtoRequest contractDtoRequest) {
        return isEqualsTaskStatus(REGISTERED, task)
                && usersNotBlocked(task.getCustomer(), executor)
                && isEqualsUserRole(EXECUTOR, executor)
                && isCorrectConfirmCodes(contractDtoRequest.getConfirmationCode(), contractDtoRequest.getRepeatConfirmationCode())
                && customerHaveEnoughMoney(task);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractDtoResponse updateContract(ContractDtoRequest contractDtoRequest) {
        ContractDtoResponse contractDtoResponse = null;
        if (contractDtoRequest.getUserId() != null) {

            User user = userService.getUserEntityById(contractDtoRequest.getUserId());
            Contract contract = getContractEntityById(contractDtoRequest.getId());

            if (allowToUpdate(user, contract)) {
                String contractStatus = (contractDtoRequest.getContractStatus() != null) ?
                        contractDtoRequest.getContractStatus() : PAID.name();
                contract.setContractStatus(getContractStatusByValue(contractStatus));
                transferFunds(contract);
                Contract save = contractRepository.save(contract);
                bookKeepingService.sendReportToBookKeeping(save);
                contractDtoResponse = enrichContractInfo(save);
            }
        }
        return contractDtoResponse;
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteContract(UUID id) {
        contractRepository.deleteById(id);
        return true;
    }


    @Override
    public ContractDtoResponse enrichContractInfo(Contract contract) {
        ContractDtoResponse contractDtoResponse = mapperFacade.map(contract, ContractDtoResponse.class);
        contractDtoResponse.setCustomer(mapperFacade.map(contract.getCustomer(), UserBaseDto.class));
        contractDtoResponse.setExecutor(mapperFacade.map(contract.getExecutor(), UserBaseDto.class));
        contractDtoResponse.setTask(taskService.enrichByUsersInfo(contract.getTask()));
        return contractDtoResponse;
    }


    @Override
    public ContractStatus getContractStatusByValue(String contractStatus) {
        return contractStatusRepository.findContractStatusByValue(contractStatus)
                .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage));
    }

    @Override
    public Contract getContractEntityById(UUID id) {
        return contractRepository.findById(id).orElseThrow(() -> new EntityRecordNotFoundException("errors.contract.notfound"));
    }

    /**
     * Метод осуществляет операция перечисления денежных средств на счет заказчика или исполнителя
     * в зависимости от статуса договора в который он переводится
     *
     * @param contract - объект договора
     */
    private void transferFunds(Contract contract) {
        if (isEqualsContractStatus(ContractStatus.ContractStatusEnum.DONE, contract)) {
            User executor = contract.getExecutor();
            executor.setWallet(executor.getWallet().add(contract.getTask().getPrice()));
        } else if (isEqualsContractStatus(TERMINATED, contract)) {
            User customer = contract.getCustomer();
            customer.setWallet(customer.getWallet().add(contract.getTask().getPrice()));
        }
    }

    /**
     * Метод проверяет достаточно ли у заказчика денег для формирования договора оказания услуги
     *
     * @param task - задание
     * @return - true - достаточно средств, false - недостаточно средств
     */
    private boolean customerHaveEnoughMoney(Task task) {
        BigDecimal customerWallet = task.getCustomer().getWallet();
        BigDecimal taskPrice = task.getPrice();
        return customerWallet.compareTo(taskPrice) >= 0;
    }

    /**
     * Метод проверяет статус пользователей
     *
     * @param customer- заказчик
     * @param executor  - исполниель
     * @return - true - пользователи не заблокированы, false - пользователи заблокированы
     */
    private boolean usersNotBlocked(User customer, User executor) {
        return !(isEqualsUserStatus(BLOCKED, customer) || isEqualsUserStatus(BLOCKED, executor));
    }


    /**
     * Метод проверяет правильность введенных кодов подтверждения
     *
     * @param code       - основной код
     * @param repeatCode - код подтверждения
     * @return - true - коды совпадают, false - коды не совпадают
     */
    private boolean isCorrectConfirmCodes(String code, String repeatCode) {
        return (code != null) && code.equals(repeatCode);
    }

    /**
     * Метод проверяет возможность обновления контракта
     *
     * @param user     - пользователь инициировавший процесс
     * @param contract - контракт
     * @return - true - пользователь не заблокирован, пользователь - заказчик, задание находится в финальном статусе,
     * контракт оплачен, false - в любом ином случае
     */
    private boolean allowToUpdate(User user, Contract contract) {
        boolean userNotBlocked = !isEqualsUserStatus(BLOCKED, user);
        boolean userIsCustomer = user.getId().equals(contract.getCustomer().getId());
        boolean contractIsPaid = isEqualsContractStatus(PAID, contract);
        boolean taskInTerminatedStatus = isEqualsTaskStatus(DONE, contract.getTask())
                || isEqualsTaskStatus(CANCELED, contract.getTask());

        return userNotBlocked && userIsCustomer && contractIsPaid && taskInTerminatedStatus;
    }


    public PageDto<ContractDtoResponse> getContracts(SearchDto<ContractSearchDto> searchDto, Pageable pageable) {
        Page<Contract> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = contractRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = contractRepository.findAll(pageable);
        }

        List<ContractDtoResponse> contractDtoResponses = page.map(this::enrichContractInfo).toList();
        return new PageDto<>(contractDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<ContractSearchDto> searchDto) {
        ContractSearchDto contractSearchDto = searchDto.searchData();
        return new CriteriaObject(contractSearchDto.getJoinOperation(), prepareRestrictionValues(contractSearchDto));
    }


    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param contractSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(ContractSearchDto contractSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit contractSearchStatus = contractSearchDto.getContractStatus();
        if (searchUnitIsValid(contractSearchStatus)) {
            ContractStatus contractStatus = contractStatusRepository
                    .findContractStatusByValue(contractSearchStatus.getValue())
                    .orElseThrow(InvalidContractStatusException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("contractStatus")
                    .setSearchOperation(contractSearchStatus.getSearchOperation())
                    .setTypedValue(contractStatus)
                    .build());
        }

        SearchUnit createdAt = contractSearchDto.getCreatedAt();
        if (searchUnitIsValid(createdAt)) {
            if (isBetweenOperation(createdAt)) {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("createdAt")
                        .setSearchOperation(createdAt.getSearchOperation())
                        .setValue(createdAt.getValue())
                        .setMinValue(createdAt.getMinValue())
                        .setMaxValue(createdAt.getMaxValue())
                        .build());
            } else {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("createdAt")
                        .setSearchOperation(createdAt.getSearchOperation())
                        .setValue(createdAt.getValue())
                        .build());
            }
        }

        return restrictionValues;
    }
}
