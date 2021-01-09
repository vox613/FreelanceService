package ru.iteco.project.service.mappers;

import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.domain.TaskStatus;

import java.util.UUID;

/**
 * Класс маппер для сущности UserStatus
 */
@Service
public class TaskStatusDtoEntityMapper implements DtoEntityMapper<TaskStatus, TaskStatusDtoRequest, TaskStatusDtoResponse> {

    @Override
    public TaskStatusDtoResponse entityToResponseDto(TaskStatus entity) {
        TaskStatusDtoResponse taskStatusDtoResponse = new TaskStatusDtoResponse();
        if (entity != null) {
            taskStatusDtoResponse.setId(entity.getId());
            taskStatusDtoResponse.setValue(entity.getValue());
            taskStatusDtoResponse.setDescription(entity.getDescription());
            taskStatusDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            taskStatusDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
        }
        return taskStatusDtoResponse;
    }

    @Override
    public TaskStatus requestDtoToEntity(TaskStatusDtoRequest requestDto) {
        TaskStatus taskStatus = new TaskStatus();
        if (requestDto != null) {
            taskStatus.setId(UUID.randomUUID());
            taskStatus.setValue(requestDto.getValue());
            taskStatus.setDescription(requestDto.getDescription());
        }
        return taskStatus;
    }
}
