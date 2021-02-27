package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.*;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidContractStatusException;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.ContractStatusRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientBaseDto;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoResponse;
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.service.util.AuthenticationUtil;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.BLOCKED;
import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.*;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.DONE;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.specification.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.specification.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {
    private static final Logger log = LogManager.getLogger(ContractServiceImpl.class.getName());


    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Contract> specificationBuilder;

    /*** Объект маппера dto <-> сущность договога */
    private final MapperFacade mapperFacade;


    public ContractServiceImpl(ContractRepository contractRepository, ClientRepository clientRepository, TaskRepository taskRepository,
                               ContractStatusRepository contractStatusRepository, MapperFacade mapperFacade, TaskService taskService,
                               SpecificationBuilder<Contract> specificationBuilder) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
        this.contractStatusRepository = contractStatusRepository;
        this.taskService = taskService;
        this.specificationBuilder = specificationBuilder;
        this.mapperFacade = mapperFacade;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
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
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ContractDtoResponse getContractById(UUID id) {
        ContractDtoResponse contractDtoResponse = null;
        Optional<Contract> optionalContract = contractRepository.findById(id);
        if (optionalContract.isPresent()) {
            Contract contract = optionalContract.get();
            contractDtoResponse = enrichContractInfo(contract);
        }
        return contractDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public ContractDtoResponse createContract(ContractDtoRequest contractDtoRequest) {
        Task task = taskRepository.findById(contractDtoRequest.getTaskId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        Client executor = clientRepository.findById(contractDtoRequest.getClientId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );

        ContractDtoResponse contractDtoResponse = null;
        if (checkClientPermissions(contractDtoRequest, executor, task)) {
            Contract contract = mapperFacade.map(contractDtoRequest, Contract.class);
            contract.setId(UUID.randomUUID());
            Client taskCustomer = contract.getCustomer();
            taskCustomer.setWallet(taskCustomer.getWallet().subtract(task.getPrice()));
            Contract save = contractRepository.save(contract);
            contractDtoResponse = enrichContractInfo(save);
        }
        return contractDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public ContractDtoResponse updateContract(ContractDtoRequest contractDtoRequest) {
        Client customer = clientRepository.findById(contractDtoRequest.getClientId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        Contract contract = contractRepository.findById(contractDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

        ContractDtoResponse contractDtoResponse = null;
        if (allowToUpdate(customer, contract)) {
            mapperFacade.map(contractDtoRequest, contract);
            transferFunds(contract);
            Contract save = contractRepository.save(contract);
            contractDtoResponse = enrichContractInfo(save);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Boolean deleteContract(UUID id) {
        contractRepository.deleteById(id);
        return true;
    }


    @Override
    public ContractDtoResponse enrichContractInfo(Contract contract) {
        ContractDtoResponse contractDtoResponse = mapperFacade.map(contract, ContractDtoResponse.class);
        contractDtoResponse.setCustomer(mapperFacade.map(contract.getCustomer(), ClientBaseDto.class));
        contractDtoResponse.setExecutor(mapperFacade.map(contract.getExecutor(), ClientBaseDto.class));
        contractDtoResponse.setTask(taskService.enrichByClientsInfo(contract.getTask()));
        return contractDtoResponse;
    }

    /**
     * Метод осуществляет операция перечисления денежных средств на счет заказчика или исполнителя
     * в зависимости от статуса договора в который он переводится
     *
     * @param contract - объект договора
     */
    private void transferFunds(Contract contract) {
        if (isEqualsContractStatus(ContractStatus.ContractStatusEnum.DONE, contract)) {
            Client executor = contract.getExecutor();
            executor.setWallet(executor.getWallet().add(contract.getTask().getPrice()));
        } else if (isEqualsContractStatus(TERMINATED, contract)) {
            Client customer = contract.getCustomer();
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
    private boolean clientsNotBlocked(Client customer, Client executor) {
        return !(ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, customer) ||
                ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, executor));
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
     * @param customer   - пользователь инициировавший процесс
     * @param contract - контракт
     * @return - true - пользователь не заблокирован, пользователь - заказчик, задание находится в финальном статусе,
     * контракт оплачен, false - в любом ином случае
     */
    private boolean allowToUpdate(Client customer, Contract contract) {
        AuthenticationUtil.userIdAndClientIdIsMatched(customer.getId());
        boolean clientNotBlocked = !ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, customer);
        boolean clientIsCustomer = customer.getId().equals(contract.getCustomer().getId());
        boolean contractIsPaid = isEqualsContractStatus(PAID, contract);
        boolean taskInTerminatedStatus = isEqualsTaskStatus(DONE, contract.getTask())
                || isEqualsTaskStatus(CANCELED, contract.getTask());

        return clientNotBlocked && clientIsCustomer && contractIsPaid && taskInTerminatedStatus;
    }

    /**
     * Метод проверяет разрешения пользователя для действия создания контракта
     * @param contractDtoRequest - модель с данными для заклюения контракта
     * @param executor - сущность исполнителя
     * @param task - сущность задания - предмета заключения контракта
     * @return - true - заключение контракта доступно, false - заключение контракта недоступно
     */
    private boolean checkClientPermissions(ContractDtoRequest contractDtoRequest, Client executor, Task task) {
        AuthenticationUtil.userIdAndClientIdIsMatched(executor.getId());
        return isEqualsTaskStatus(REGISTERED, task)
                && clientsNotBlocked(task.getCustomer(), executor)
                && ClientRole.ClientRoleEnum.isEqualsClientRole(EXECUTOR, executor)
                && isCorrectConfirmCodes(contractDtoRequest.getConfirmationCode(), contractDtoRequest.getRepeatConfirmationCode())
                && customerHaveEnoughMoney(task);
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
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
