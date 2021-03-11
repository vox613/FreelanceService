package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;


@ApiModel(description = "Данные модели задания для запроса")
public class TaskDtoRequest extends TaskBaseDto {

    @ApiModelProperty(value = "Решение задания", example = "Решение задания", allowEmptyValue = true)
    private String taskDecision;

    @ApiModelProperty(value = "Список ошибок валидации задания", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public TaskDtoRequest() {
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
