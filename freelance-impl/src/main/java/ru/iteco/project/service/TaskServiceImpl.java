package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.*;
import ru.iteco.project.exception.*;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.TaskStatusRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientBaseDto;
import ru.iteco.project.resource.dto.TaskDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.searching.TaskSearchDto;
import ru.iteco.project.service.util.AuthenticationUtil;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.BLOCKED;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;


/**
 * Класс реализует функционал сервисного слоя для работы с заданиями
 */
@Service
@PropertySource(value = {"classpath:errors.properties"}, encoding = "UTF-8")
public class TaskServiceImpl implements TaskService {

    @Value("${errors.client.role.operation.unavailable}")
    private String unavailableOperationMessage;


    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект маппера dto <-> сущность задания */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Task> specificationBuilder;


    public TaskServiceImpl(TaskRepository taskRepository, ClientRepository clientRepository, ContractRepository contractRepository,
                           TaskStatusRepository taskStatusRepository, MapperFacade mapperFacade, SpecificationBuilder<Task> specificationBuilder) {
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
        this.contractRepository = contractRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.mapperFacade = mapperFacade;
        this.specificationBuilder = specificationBuilder;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<TaskDtoResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::enrichByClientsInfo)
                .collect(Collectors.toList());
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<TaskDtoResponse> getAllClientTasks(UUID clientId) {
        return taskRepository.findTasksByCustomerId(clientId).stream()
                .map(this::enrichByClientsInfo)
                .collect(Collectors.toList());
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TaskDtoResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        return enrichByClientsInfo(task);
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public TaskDtoResponse createTask(TaskDtoRequest taskDtoRequest) {
        checkPossibilityToCreate(taskDtoRequest);
        Task task = mapperFacade.map(taskDtoRequest, Task.class);
        task.setId(UUID.randomUUID());
        Task save = taskRepository.save(task);
        return enrichByClientsInfo(save);
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public TaskDtoResponse updateTask(TaskDtoRequest taskDtoRequest) {
        Task task = taskRepository.findById(taskDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkUpdatedData(taskDtoRequest, task);
        mapperFacade.map(taskDtoRequest, task);
        Task save = taskRepository.save(task);
        return enrichByClientsInfo(save);
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Boolean deleteTask(UUID id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkPermissions(task.getCustomer().getId());
        taskStatusIsTerminated(task.getTaskStatus().getValue());
        contractRepository.findContractByTask(task).ifPresent(this::deleteContractForTask);
        taskRepository.deleteById(id);
        return true;
    }

    private void checkPermissions(UUID clientId) {
        if (AuthenticationUtil.userHasRole(AuthenticationUtil.ROLE_USER)) {
            AuthenticationUtil.userIdAndClientIdIsMatched(clientId);
        }
    }

    private void taskStatusIsTerminated(String taskStatus) {
        if (!TaskStatus.TaskStatusEnum.valueOf(taskStatus).isTerminated()) {
            throw new InvalidTaskStatusException("errors.task.status.notTerminated");
        }
    }

    private void deleteContractForTask(Contract contract) {
        if (!ContractStatus.ContractStatusEnum.valueOf(contract.getContractStatus().getValue()).isTerminated()) {
            throw new InvalidContractStatusException("errors.contract.status.notTerminated");
        }
        contractRepository.delete(contract);
    }

    @Override
    public void checkPossibilityToCreate(TaskDtoRequest taskDtoRequest) {
        Client client = clientRepository.findById(AuthenticationUtil.getUserPrincipalId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        if (ClientRole.ClientRoleEnum.isEqualsClientRole(EXECUTOR, client) ||
                ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, client)) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }
    }

    @Override
    public void checkUpdatedData(TaskDtoRequest taskDtoRequest, Task task) {
        if (isEqualsTaskStatus(CANCELED, task) || isEqualsTaskStatus(DONE, task)) {
            throw new InvalidTaskStatusException("errors.task.status.terminated");
        }

        Client client = clientRepository.findById(AuthenticationUtil.getUserPrincipalId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        clientNotBlocked(client);
        boolean clientIsCustomerAndTaskOnCustomer = client.getId().equals(task.getCustomer().getId()) &&
                (isEqualsTaskStatus(REGISTERED, task) || isEqualsTaskStatus(ON_CHECK, task));
        boolean clientIsExecutorAndTaskOnExecutor = (task.getExecutor() != null) &&
                client.getId().equals(task.getExecutor().getId()) &&
                (isEqualsTaskStatus(IN_PROGRESS, task) || isEqualsTaskStatus(ON_FIX, task));

        if (!(clientIsCustomerAndTaskOnCustomer || clientIsExecutorAndTaskOnExecutor)) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }

        String newTaskStatus = taskDtoRequest.getTaskStatus();
        boolean haveExecutor = task.getExecutor() != null;
        if ((haveExecutor && isEqualsTaskStatus(CANCELED, newTaskStatus)) ||
                (haveExecutor && isEqualsTaskStatus(REGISTERED, newTaskStatus)) ||
                (!haveExecutor && isEqualsTaskStatus(ON_FIX, newTaskStatus))) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
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
     * Метод формирует ответ TaskDtoResponse и обогащает его данными о заказчике и исполнителе
     *
     * @param task - объект задания
     * @return - объект TaskDtoResponse с подготовленными данными о задании, исполнителе и заказчике
     */
    @Override
    public TaskDtoResponse enrichByClientsInfo(Task task) {
        TaskDtoResponse taskDtoResponse = mapperFacade.map(task, TaskDtoResponse.class);
        taskDtoResponse.setCustomer(mapperFacade.map(task.getCustomer(), ClientBaseDto.class));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(mapperFacade.map(task.getExecutor(), ClientBaseDto.class));
        }
        return taskDtoResponse;
    }


    /**
     * Метод проверяет возможность обновления контракта
     *
     * @param client - пользователь инициировавший процесс
     * @param task   - задание
     */
    private void allowToUpdate(Client client, Task task) {
        boolean clientNotBlocked = !ClientStatus.ClientStatusEnum.isEqualsClientStatus(BLOCKED, client);
        boolean clientIsCustomerAndTaskOnCustomer = client.getId().equals(task.getCustomer().getId()) &&
                (isEqualsTaskStatus(REGISTERED, task) || isEqualsTaskStatus(ON_CHECK, task));
        boolean clientIsExecutorAndTaskOnExecutor = (task.getExecutor() != null) &&
                client.getId().equals(task.getExecutor().getId()) &&
                (isEqualsTaskStatus(IN_PROGRESS, task) || isEqualsTaskStatus(ON_FIX, task));

        if (clientIsCustomerAndTaskOnCustomer) {
            AuthenticationUtil.userIdAndClientIdIsMatched(task.getCustomer().getId());
        } else if (clientIsExecutorAndTaskOnExecutor) {
            AuthenticationUtil.userIdAndClientIdIsMatched(task.getExecutor().getId());
        }

        boolean isAllowed = clientNotBlocked && (clientIsCustomerAndTaskOnCustomer || clientIsExecutorAndTaskOnExecutor);
        if (!isAllowed) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PageDto<TaskDtoResponse> getTasks(SearchDto<TaskSearchDto> searchDto, Pageable pageable) {
        Page<Task> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = taskRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = taskRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
        }

        List<TaskDtoResponse> taskDtoResponses = page.map(this::enrichByClientsInfo).toList();
        return new PageDto<>(taskDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<TaskSearchDto> searchDto) {
        TaskSearchDto taskSearchDto = searchDto.searchData();
        return new CriteriaObject(taskSearchDto.getJoinOperation(), prepareRestrictionValues(taskSearchDto));
    }


    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param taskSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(TaskSearchDto taskSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit taskSearchStatus = taskSearchDto.getTaskStatus();
        prepareRestrictionValue(restrictionValues, taskSearchStatus, "taskStatus",
                o -> taskStatusRepository.findTaskStatusByValue(taskSearchStatus.getValue())
                        .orElseThrow(InvalidTaskStatusException::new));

        SearchUnit createdAt = taskSearchDto.getCreatedAt();
        prepareRestrictionValue(restrictionValues, createdAt, "createdAt", searchUnit -> createdAt.getValue());

        SearchUnit price = taskSearchDto.getPrice();
        prepareRestrictionValue(restrictionValues, price, "price", searchUnit -> price.getValue());

        SearchUnit description = taskSearchDto.getDescription();
        prepareRestrictionValue(restrictionValues, description, "description", searchUnit -> description.getValue());

        SearchUnit taskCompletionDate = taskSearchDto.getTaskCompletionDate();
        prepareRestrictionValue(restrictionValues, taskCompletionDate, "taskCompletionDate", searchUnit -> taskCompletionDate.getValue());


        return restrictionValues;
    }
}
