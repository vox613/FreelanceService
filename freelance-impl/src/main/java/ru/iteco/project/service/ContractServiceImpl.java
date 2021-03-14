package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.*;
import ru.iteco.project.exception.*;
import ru.iteco.project.repository.*;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientBaseDto;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoResponse;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.service.util.AuthenticationUtil;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.BLOCKED;
import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.*;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.DONE;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;


/**
 * Класс реализует функционал сервисного слоя для работы с контрактами
 */
@Service
public class ContractServiceImpl implements ContractService {

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Contract> specificationBuilder;

    /*** Объект маппера dto <-> сущность договога */
    private final MapperFacade mapperFacade;


    public ContractServiceImpl(ContractRepository contractRepository, ClientRepository clientRepository, TaskRepository taskRepository,
                               TaskStatusRepository taskStatusRepository, ContractStatusRepository contractStatusRepository,
                               MapperFacade mapperFacade, TaskService taskService,
                               SpecificationBuilder<Contract> specificationBuilder) {
        this.contractRepository = contractRepository;
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
        this.taskStatusRepository = taskStatusRepository;
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
        return contractRepository.findAll()
                .stream()
                .map(contract -> mapperFacade.map(contract, ContractDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ContractDtoResponse getContractById(UUID id) {
        Contract contract = contractRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        return mapperFacade.map(contract, ContractDtoResponse.class);
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

        checkPossibilityToCreate(contractDtoRequest);
        Contract contract = mapperFacade.map(contractDtoRequest, Contract.class);
        contract.setId(UUID.randomUUID());
        Client taskCustomer = contract.getCustomer();
        taskCustomer.setWallet(taskCustomer.getWallet().subtract(task.getPrice()));
        Contract save = contractRepository.save(contract);
        return mapperFacade.map(save, ContractDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public ContractDtoResponse updateContract(ContractDtoRequest contractDtoRequest) {
        Contract contract = contractRepository.findById(contractDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

        checkUpdatedData(contractDtoRequest, contract);
        mapperFacade.map(contractDtoRequest, contract);
        transferFunds(contract);
        Contract save = contractRepository.save(contract);
        return mapperFacade.map(save, ContractDtoResponse.class);
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
        Contract contract = contractRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        if (!ContractStatus.ContractStatusEnum.valueOf(contract.getContractStatus().getValue()).isTerminated()) {
            throw new InvalidContractStatusException("errors.contract.status.notTerminated");
        }
        contractRepository.deleteById(id);
        return true;
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
            contract.getTask().setTaskStatus(taskStatusRepository.findTaskStatusByValue(CANCELED.name()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")));
        }
    }

    /**
     * Метод проверяет достаточно ли у заказчика денег для формирования договора оказания услуги
     *
     * @param task - задание
     */
    private void customerHaveEnoughMoney(Task task) {
        BigDecimal customerWallet = task.getCustomer().getWallet();
        BigDecimal taskPrice = task.getPrice();
        if (customerWallet.compareTo(taskPrice) < 0) {
            throw new InsufficientFundsException("errors.client.wallet.notEnough");
        }
    }

    /**
     * Метод проверяет статус клиента
     *
     * @param client - клиент
     */
    private void clientNotBlocked(Client client) {
        if (ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, client)) {
            throw new InvalidClientStatusException("errors.client.status.blocked");
        }
    }


    /**
     * Метод проверяет правильность введенных кодов подтверждения
     *
     * @param code       - основной код
     * @param repeatCode - код подтверждения
     */
    private void checkConfirmCodes(String code, String repeatCode) {
        if ((code == null) || !code.equals(repeatCode)) {
            throw new IllegalArgumentException("errors.confirmation.code.mismatched");
        }
    }

    @Override
    public void checkUpdatedData(ContractDtoRequest contractDtoRequest, Contract contract) {
        if (!isEqualsContractStatus(PAID, contract)) {
            throw new InvalidContractStatusException("errors.contract.status.terminated");
        }

        Client customer = clientRepository.findById(AuthenticationUtil.getUserPrincipalId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

        clientNotBlocked(customer);
        if (!customer.getId().equals(contract.getCustomer().getId())) {
            throw new InvalidClientStatusException("errors.client.role.operation.unavailable");
        }

        if (ContractStatus.ContractStatusEnum.isEqualsContractStatus(ContractStatus.ContractStatusEnum.DONE, contractDtoRequest.getContractStatus())) {
            if (!(isEqualsTaskStatus(DONE, contract.getTask()) || isEqualsTaskStatus(CANCELED, contract.getTask()))) {
                throw new InvalidTaskStatusException("errors.task.status.invalid");
            }
        }
    }

    @Override
    public void checkPossibilityToCreate(ContractDtoRequest contractDtoRequest) {
        AuthenticationUtil.userIdAndClientIdIsMatched(contractDtoRequest.getExecutorId());
        Task task = taskRepository.findById(contractDtoRequest.getTaskId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        Client executor = clientRepository.findById(contractDtoRequest.getExecutorId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );

        if (!isEqualsTaskStatus(REGISTERED, task)) {
            throw new InvalidTaskStatusException("errors.task.status.invalid");
        }
        if (!ClientRole.ClientRoleEnum.isEqualsClientRole(EXECUTOR, executor)) {
            throw new InvalidClientRoleException("errors.client.role.operation.unavailable");
        }
        clientNotBlocked(task.getCustomer());
        clientNotBlocked(executor);
        checkConfirmCodes(contractDtoRequest.getConfirmationCode(), contractDtoRequest.getRepeatConfirmationCode());
        customerHaveEnoughMoney(task);
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ContractDtoResponse> getContracts(SearchDto<ContractSearchDto> searchDto, Pageable pageable) {
        Page<Contract> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = contractRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = contractRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
        }

        List<ContractDtoResponse> contractDtoResponses = page
                .map(contract -> mapperFacade.map(contract, ContractDtoResponse.class)).toList();
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
        prepareRestrictionValue(restrictionValues, contractSearchStatus, "contractStatus",
                o -> contractStatusRepository.findContractStatusByValue(contractSearchStatus.getValue())
                        .orElseThrow(InvalidContractStatusException::new));

        SearchUnit createdAt = contractSearchDto.getCreatedAt();
        prepareRestrictionValue(restrictionValues, createdAt, "createdAt", searchUnit -> createdAt.getValue());

        return restrictionValues;
    }
}
