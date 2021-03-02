package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;


@ApiModel(description = "Данные модели задания для запроса")
public class TaskDtoRequest extends TaskBaseDto {


    @ApiModelProperty(value = "Идентификатор пользователя совершающего действие",
            example = "748b310e-486d-11eb-94e0-0242ac130002",
            required = true)
    private UUID clientId;

    @ApiModelProperty(value = "Решение задания", example = "Решение задания", allowEmptyValue = true)
    private String taskDecision;

    @ApiModelProperty(value = "Список ошибок валидации задания", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public TaskDtoRequest() {
    }


    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
