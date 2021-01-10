package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.exception.UnavailableRoleOperationException;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.TaskStatusRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.resource.dto.TaskDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.resource.searching.TaskSearchDto;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.BLOCKED;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.isEqualsUserStatus;
import static ru.iteco.project.service.specifications.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;

/**
 * Класс реализует функционал сервисного слоя для работы с заданиями
 */
@Service
@PropertySource(value = {"classpath:errors.properties"})
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LogManager.getLogger(TaskServiceImpl.class.getName());

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;


    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект маппера dto <-> сущность задания */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Task> specificationBuilder;


    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ContractRepository contractRepository,
                           TaskStatusRepository taskStatusRepository, MapperFacade mapperFacade, SpecificationBuilder<Task> specificationBuilder) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
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
    public List<TaskDtoResponse> getAllTasks() {
        ArrayList<TaskDtoResponse> taskDtoResponses = new ArrayList<>();
        for (Task task : taskRepository.findAll()) {
            taskDtoResponses.add(enrichByUsersInfo(task));
        }
        return taskDtoResponses;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDtoResponse> getAllUserTasks(UUID userId) {
        return taskRepository.findTasksByCustomerId(userId).stream()
                .map(this::enrichByUsersInfo)
                .collect(Collectors.toList());
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public TaskDtoResponse getTaskById(UUID id) {
        TaskDtoResponse taskDtoResponse = null;
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            taskDtoResponse = enrichByUsersInfo(task);
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskDtoResponse createTask(TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        Optional<User> userOptional = userRepository.findById(taskDtoRequest.getUserId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserPermissions(user);
            Task task = mapperFacade.map(taskDtoRequest, Task.class);
            task.setId(UUID.randomUUID());
            Task save = taskRepository.save(task);
            taskDtoResponse = enrichByUsersInfo(save);
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskDtoResponse updateTask(TaskDtoRequest taskDtoRequest) {
        TaskDtoResponse taskDtoResponse = null;
        if (taskDtoRequest.getUserId() != null
                && taskRepository.existsById(taskDtoRequest.getUserId())) {

            Optional<User> userOptional = userRepository.findById(taskDtoRequest.getUserId());
            Optional<Task> taskById = taskRepository.findById(taskDtoRequest.getUserId());
            if (userOptional.isPresent() && taskById.isPresent()) {
                User user = userOptional.get();
                Task task = taskById.get();
                if (allowToUpdate(user, task)) {
                    mapperFacade.map(taskDtoRequest, task);
                    Task save = taskRepository.save(task);
                    taskDtoResponse = enrichByUsersInfo(save);
                }
            }
        }
        return taskDtoResponse;
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteTask(UUID id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Optional<Contract> optionalContract = contractRepository.findContractByTask(optionalTask.get());
            optionalContract.ifPresent(contractRepository::delete);
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод формирует ответ TaskDtoResponse и обогащает его данными о заказчике и исполнителе
     *
     * @param task - объект задания
     * @return - объект TaskDtoResponse с подготовленными данными о задании, исполнителе и заказчике
     */
    @Override
    public TaskDtoResponse enrichByUsersInfo(Task task) {
        TaskDtoResponse taskDtoResponse = mapperFacade.map(task, TaskDtoResponse.class);
        taskDtoResponse.setCustomer(mapperFacade.map(task.getCustomer(), UserBaseDto.class));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(mapperFacade.map(task.getExecutor(), UserBaseDto.class));
        }
        return taskDtoResponse;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    /**
     * Метод проверяет возможность обновления контракта
     *
     * @param user - пользователь инициировавший процесс
     * @param task - задание
     * @return - true - пользователь не заблокирован, роль пользователя позволяет менять статаус задания,
     * false - в любом ином случае
     */
    private boolean allowToUpdate(User user, Task task) {
        boolean userNotBlocked = !isEqualsUserStatus(BLOCKED, user);
        boolean userIsCustomerAndTaskOnCustomer = user.getId().equals(task.getCustomer().getId()) &&
                (isEqualsTaskStatus(REGISTERED, task) || isEqualsTaskStatus(ON_CHECK, task));
        boolean userIsExecutorAndTaskOnExecutor = (task.getExecutor() != null) &&
                user.getId().equals(task.getExecutor().getId()) &&
                (isEqualsTaskStatus(IN_PROGRESS, task) || isEqualsTaskStatus(ON_FIX, task));

        return userNotBlocked && (userIsCustomerAndTaskOnCustomer || userIsExecutorAndTaskOnExecutor);
    }


    /**
     * Метод проверяет доступна ли для пользователя операция создания задания
     *
     * @param user - сущность пользователя
     */
    private void checkUserPermissions(User user) {
        if (isEqualsUserRole(EXECUTOR, user) || isEqualsUserStatus(BLOCKED, user)) {
            throw new UnavailableRoleOperationException(unavailableOperationMessage);
        }
    }

    public PageDto<TaskDtoResponse> getTasks(SearchDto<TaskSearchDto> searchDto, Pageable pageable) {
        Page<Task> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = taskRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = taskRepository.findAll(pageable);
        }

        List<TaskDtoResponse> taskDtoResponses = page.map(this::enrichByUsersInfo).toList();
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
        if (searchUnitIsValid(taskSearchStatus)) {
            TaskStatus taskStatus = taskStatusRepository
                    .findTaskStatusByValue(taskSearchStatus.getValue())
                    .orElseThrow(InvalidTaskStatusException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("taskStatus")
                    .setTypedValue(taskStatus)
                    .setSearchOperation(taskSearchStatus.getSearchOperation())
                    .build());

        }

        SearchUnit createdAt = taskSearchDto.getCreatedAt();
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


        SearchUnit price = taskSearchDto.getPrice();
        if (searchUnitIsValid(price)) {
            if (isBetweenOperation(price)) {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("price")
                        .setSearchOperation(price.getSearchOperation())
                        .setValue(price.getValue())
                        .setMinValue(price.getMinValue())
                        .setMaxValue(price.getMaxValue())
                        .build());
            } else {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("price")
                        .setSearchOperation(price.getSearchOperation())
                        .setValue(price.getValue())
                        .build());
            }
        }


        SearchUnit description = taskSearchDto.getDescription();
        if (searchUnitIsValid(description)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("description")
                    .setSearchOperation(description.getSearchOperation())
                    .setValue(description.getValue())
                    .build());
        }


        SearchUnit taskCompletionDate = taskSearchDto.getTaskCompletionDate();
        if (searchUnitIsValid(taskCompletionDate)) {
            if (isBetweenOperation(taskCompletionDate)) {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("taskCompletionDate")
                        .setSearchOperation(taskCompletionDate.getSearchOperation())
                        .setValue(taskCompletionDate.getValue())
                        .setMinValue(taskCompletionDate.getMinValue())
                        .setMaxValue(taskCompletionDate.getMaxValue())
                        .build());
            } else {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("taskCompletionDate")
                        .setSearchOperation(taskCompletionDate.getSearchOperation())
                        .setValue(taskCompletionDate.getValue())
                        .build());
            }
        }

        return restrictionValues;
    }
}
