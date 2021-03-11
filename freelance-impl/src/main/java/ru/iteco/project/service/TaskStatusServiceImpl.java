package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidSearchExpressionException;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.TaskStatusRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;
import ru.iteco.project.resource.dto.TaskStatusDtoResponse;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами заданий
 */
@Service
public class TaskStatusServiceImpl implements TaskStatusService {

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект маппера dto <-> сущность статуса пользователя */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<TaskStatus> specificationBuilder;


    public TaskStatusServiceImpl(TaskStatusRepository taskStatusRepository, TaskRepository taskRepository,
                                 ClientRepository clientRepository, TaskService taskService, MapperFacade mapperFacade,
                                 SpecificationBuilder<TaskStatus> specificationBuilder) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
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
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDtoResponse getTaskStatusById(UUID id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        return mapperFacade.map(taskStatus, TaskStatusDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDtoResponse createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest) {
        checkPossibilityToCreate(taskStatusDtoRequest);
        TaskStatus newTaskStatus = mapperFacade.map(taskStatusDtoRequest, TaskStatus.class);
        newTaskStatus.setId(UUID.randomUUID());
        TaskStatus save = taskStatusRepository.save(newTaskStatus);
        return mapperFacade.map(save, TaskStatusDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public TaskStatusDtoResponse updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest) {
        TaskStatus taskStatus = taskStatusRepository.findById(taskStatusDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        checkUpdatedData(taskStatusDtoRequest, taskStatus);
        mapperFacade.map(taskStatusDtoRequest, taskStatus);
        TaskStatus save = taskStatusRepository.save(taskStatus);
        return mapperFacade.map(save, TaskStatusDtoResponse.class);
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return List<TaskStatusDtoResponse> - список всех статусов заданий
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteTaskStatus(UUID id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        taskRepository.findAllByTaskStatus(taskStatus)
                .forEach(task -> taskService.deleteTask(task.getId()));
        taskStatusRepository.deleteById(id);
        return true;
    }


    @Override
    public void checkPossibilityToCreate(TaskStatusDtoRequest taskStatusDtoRequest) {
        if (taskStatusRepository.existsTaskStatusByValue(taskStatusDtoRequest.getValue())) {
            throw new IllegalArgumentException("errors.persistence.entity.exist");
        }
    }

    @Override
    public void checkUpdatedData(TaskStatusDtoRequest taskStatusDtoRequest, TaskStatus taskStatus) {
        String value = taskStatusDtoRequest.getValue();
        if (!value.equals(taskStatus.getValue())) {
            checkPossibilityToCreate(taskStatusDtoRequest);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<TaskStatusDtoResponse> getStatus(SearchDto<TaskStatusSearchDto> searchDto, Pageable pageable) {
        Page<TaskStatus> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = taskStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = taskStatusRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
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
        prepareRestrictionValue(restrictionValues, value, "value", searchUnit -> value.getValue());

        SearchUnit description = taskStatusSearchDto.getDescription();
        prepareRestrictionValue(restrictionValues, description, "description", searchUnit -> description.getValue());

        return restrictionValues;
    }
}
