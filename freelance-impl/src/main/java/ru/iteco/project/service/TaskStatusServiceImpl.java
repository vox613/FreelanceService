package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.TaskStatusRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;
import ru.iteco.project.resource.dto.TaskStatusDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами заданий
 */
@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    private static final Logger log = LogManager.getLogger(TaskStatusServiceImpl.class.getName());


    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект маппера dto <-> сущность статуса пользователя */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<TaskStatus> specificationBuilder;


    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository,
                                 UserRepository userRepository, TaskService taskService, MapperFacade mapperFacade,
                                 SpecificationBuilder<TaskStatus> specificationBuilder) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
        this.mapperFacade = mapperFacade;
        this.specificationBuilder = specificationBuilder;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public TaskStatusDtoResponse getTaskStatusById(UUID id) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        Optional<TaskStatus> optionalTaskStatusById = taskStatusRepository.findById(id);
        if (optionalTaskStatusById.isPresent()) {
            TaskStatus taskStatus = optionalTaskStatusById.get();
            taskStatusDtoResponse = mapperFacade.map(taskStatus, TaskStatusDtoResponse.class);
        }
        return taskStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskStatusDtoResponse createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        if (operationIsAllow(taskStatusDtoRequest)) {
            TaskStatus newTaskStatus = mapperFacade.map(taskStatusDtoRequest, TaskStatus.class);
            newTaskStatus.setId(UUID.randomUUID());
            TaskStatus save = taskStatusRepository.save(newTaskStatus);
            taskStatusDtoResponse = mapperFacade.map(save, TaskStatusDtoResponse.class);
        }
        return taskStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TaskStatusDtoResponse updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        if (operationIsAllow(taskStatusDtoRequest) &&
                Objects.equals(id, taskStatusDtoRequest.getId()) &&
                taskStatusRepository.existsById(taskStatusDtoRequest.getId())) {

            TaskStatus taskStatus = taskStatusRepository.findById(taskStatusDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

            mapperFacade.map(taskStatusDtoRequest, taskStatus);
            TaskStatus save = taskStatusRepository.save(taskStatus);
            taskStatusDtoResponse = mapperFacade.map(save, TaskStatusDtoResponse.class);
        }
        return taskStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskStatusDtoResponse> getAllTasksStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatus -> mapperFacade.map(taskStatus, TaskStatusDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteTaskStatus(UUID id) {
        Optional<TaskStatus> taskStatusById = taskStatusRepository.findById(id);
        if (taskStatusById.isPresent()) {
            Collection<Task> allTasksWithStatus = taskRepository.findAllByTaskStatus(taskStatusById.get());
            allTasksWithStatus.forEach(task -> taskService.deleteTask(task.getId()));
            taskStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param taskStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(TaskStatusDtoRequest taskStatusDtoRequest) {
        if ((taskStatusDtoRequest != null) && (taskStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(taskStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }


    public PageDto<TaskStatusDtoResponse> getStatus(SearchDto<TaskStatusSearchDto> searchDto, Pageable pageable) {
        Page<TaskStatus> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = taskStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = taskStatusRepository.findAll(pageable);
        }

        List<TaskStatusDtoResponse> TaskStatusDtoResponses = page
                .map(taskStatus -> mapperFacade.map(taskStatus, TaskStatusDtoResponse.class))
                .toList();
        return new PageDto<>(TaskStatusDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<TaskStatusSearchDto> searchDto) {
        TaskStatusSearchDto taskStatusSearchDto = searchDto.searchData();
        return new CriteriaObject(taskStatusSearchDto.getJoinOperation(), prepareRestrictionValues(taskStatusSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param taskStatusSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(TaskStatusSearchDto taskStatusSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit value = taskStatusSearchDto.getValue();
        if (searchUnitIsValid(value)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("value")
                    .setTypedValue(value.getValue())
                    .setSearchOperation(value.getSearchOperation())
                    .build());
        }

        SearchUnit description = taskStatusSearchDto.getDescription();
        if (searchUnitIsValid(description)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("description")
                    .setValue(description.getValue())
                    .setSearchOperation(description.getSearchOperation())
                    .build());
        }
        return restrictionValues;
    }
}
