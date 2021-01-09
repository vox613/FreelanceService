package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.SearchUnit;
import ru.iteco.project.controller.searching.TaskSearchDto;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.exception.UnavailableRoleOperationException;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.service.mappers.TaskDtoEntityMapper;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.*;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.EXECUTOR;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.*;
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

    /*** Объект маппера dto задания в сущность задания */
    private final TaskDtoEntityMapper taskMapper;

    /*** Объект маппера dto пользователя в сущность пользователя */
    private final UserDtoEntityMapper userMapper;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Task> specificationBuilder;


    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ContractRepository contractRepository,
                           TaskDtoEntityMapper taskMapper, UserDtoEntityMapper userMapper, SpecificationBuilder<Task> specificationBuilder) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.taskMapper = taskMapper;
        this.userMapper = userMapper;
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
        Optional<User> userOptional = userRepository.findById(taskDtoRequest.getCustomerId());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserPermissions(user);
            userMapper.updateUserStatus(user, ACTIVE);
            Task task = taskMapper.requestDtoToEntity(taskDtoRequest);
            task.setCustomer(user);
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
                    taskMapper.requestDtoToEntity(taskDtoRequest, task, user.getRole().getValue());
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
        TaskDtoResponse taskDtoResponse = taskMapper.entityToResponseDto(task);
        taskDtoResponse.setCustomer(userMapper.entityToResponseDto(task.getCustomer()));
        if (task.getExecutor() != null) {
            taskDtoResponse.setExecutor(userMapper.entityToResponseDto(task.getExecutor()));
        }
        return taskDtoResponse;
    }

    public TaskDtoEntityMapper getTaskMapper() {
        return taskMapper;
    }

    public UserDtoEntityMapper getUserMapper() {
        return userMapper;
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
            TaskStatus taskStatus = taskMapper.getTaskStatusRepository()
                    .findTaskStatusByValue(taskSearchStatus.getValue())
                    .orElseThrow(InvalidTaskStatusException::new);

            restrictionValues.add(
                    new CriteriaObject.RestrictionValues<TaskStatus>(
                            "taskStatus",
                            taskSearchStatus.getSearchOperation(),
                            taskStatus
                    )
            );
        }

        SearchUnit createdAt = taskSearchDto.getCreatedAt();
        if (searchUnitIsValid(createdAt)) {
            if (isBetweenOperation(createdAt)) {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "createdAt",
                                createdAt.getSearchOperation(),
                                createdAt.getValue(),
                                createdAt.getMinValue(),
                                createdAt.getMaxValue()
                        )
                );
            } else {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "createdAt",
                                createdAt.getValue(),
                                createdAt.getSearchOperation()
                        )
                );
            }
        }


        SearchUnit price = taskSearchDto.getPrice();
        if (searchUnitIsValid(price)) {
            if (isBetweenOperation(price)) {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<BigDecimal>(
                                "price",
                                price.getSearchOperation(),
                                price.getValue(),
                                price.getMinValue(),
                                price.getMaxValue()
                        )
                );
            } else {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<BigDecimal>(
                                "price",
                                price.getValue(),
                                price.getSearchOperation()
                        )
                );
            }
        }


        SearchUnit description = taskSearchDto.getDescription();
        if (searchUnitIsValid(description)) {
            restrictionValues.add(
                    new CriteriaObject.RestrictionValues<String>(
                            "description",
                            description.getValue(),
                            description.getSearchOperation()
                    )
            );
        }


        SearchUnit taskCompletionDate = taskSearchDto.getTaskCompletionDate();
        if (searchUnitIsValid(taskCompletionDate)) {
            if (isBetweenOperation(taskCompletionDate)) {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "taskCompletionDate",
                                taskCompletionDate.getSearchOperation(),
                                taskCompletionDate.getValue(),
                                taskCompletionDate.getMinValue(),
                                taskCompletionDate.getMaxValue()
                        )
                );
            } else {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "taskCompletionDate",
                                taskCompletionDate.getValue(),
                                taskCompletionDate.getSearchOperation()
                        )
                );
            }
        }

        return restrictionValues;
    }
}
