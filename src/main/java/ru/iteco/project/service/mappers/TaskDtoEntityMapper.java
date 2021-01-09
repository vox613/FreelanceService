package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskDtoRequest;
import ru.iteco.project.controller.dto.TaskDtoResponse;
import ru.iteco.project.domain.Task;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.repository.TaskStatusRepository;

import java.util.UUID;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.ON_CHECK;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.REGISTERED;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.*;

/**
 * Класс маппер для сущности Task
 */
@Service
@PropertySource(value = {"classpath:errors.properties"})
public class TaskDtoEntityMapper implements DtoEntityMapper<Task, TaskDtoRequest, TaskDtoResponse> {

    @Value("${errors.task.status.invalid}")
    private String invalidTaskStatusMessage;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;


    public TaskDtoEntityMapper(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public TaskDtoResponse entityToResponseDto(Task entity) {
        TaskDtoResponse taskDtoResponse = new TaskDtoResponse();
        if (entity != null) {
            taskDtoResponse.setId(entity.getId());
            taskDtoResponse.setCustomerId(entity.getCustomer().getId());
            taskDtoResponse.setName(entity.getTitle());
            taskDtoResponse.setDescription(entity.getDescription());
            taskDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            taskDtoResponse.setTaskCompletionDate(DateTimeMapper.objectToString(entity.getTaskCompletionDate()));
            taskDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
            taskDtoResponse.setPrice(entity.getPrice());
            taskDtoResponse.setTaskStatus(entity.getTaskStatus().getValue());
            taskDtoResponse.setTaskDecision(entity.getTaskDecision());
            if (entity.getExecutor() != null) {
                taskDtoResponse.setExecutorId(entity.getExecutor().getId());
            }
        }
        return taskDtoResponse;
    }


    @Override
    public Task requestDtoToEntity(TaskDtoRequest requestDto) {
        Task task = new Task();
        if (requestDto != null) {
            task.setId(UUID.randomUUID());
            task.setTitle(requestDto.getName());
            task.setDescription(requestDto.getDescription());
            task.setTaskCompletionDate(DateTimeMapper.stringToObject(requestDto.getTaskCompletionDate()));
            task.setPrice(requestDto.getPrice());

            task.setTaskStatus(taskStatusRepository.findTaskStatusByValue(REGISTERED.name())
                    .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage)));
            task.setTaskDecision(requestDto.getTaskDecision());
        }

        return task;
    }

    /**
     * Метод обновляет данными запроса информацию в Задании в зависимости от роли пользователя, инициировавщего процесс
     *
     * @param requestDto - запрос с данными для обновления
     * @param task       - сущность Задания для обновления
     * @param role       - роль пользователя, инициировавщего процесс
     */
    public void requestDtoToEntity(TaskDtoRequest requestDto, Task task, String role) {
        if (requestDto != null) {
            if (isEqualsUserRole(CUSTOMER, role)) {
                task.setTitle(requestDto.getName());
                task.setDescription(requestDto.getDescription());
                task.setTaskCompletionDate(DateTimeMapper.stringToObject(requestDto.getTaskCompletionDate()));
                task.setPrice(requestDto.getPrice());
                if (requestDto.getTaskStatus() != null) {
                    task.setTaskStatus(taskStatusRepository.findTaskStatusByValue(requestDto.getTaskStatus())
                            .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage)));
                }
            } else if (isEqualsUserRole(EXECUTOR, role)) {
                task.setTaskDecision(requestDto.getTaskDecision());
                task.setTaskStatus(taskStatusRepository.findTaskStatusByValue(ON_CHECK.name())
                        .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage)));
            }
        }
    }

    public TaskStatusRepository getTaskStatusRepository() {
        return taskStatusRepository;
    }
}
