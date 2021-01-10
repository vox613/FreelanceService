package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Данные модели задания для ответа")
public class TaskDtoResponse extends TaskBaseDto {

    @ApiModelProperty(value = "Решение задания",
            example = "Решение задания",
            required = true,
            allowEmptyValue = true)
    private String taskDecision;

    @ApiModelProperty(value = "Модель заказчика (в формате UserBaseDto)", required = true)
    private UserBaseDto customer;

    @ApiModelProperty(value = "Модель исполнителя (в формате UserBaseDto)")
    private UserBaseDto executor;

    @ApiModelProperty(value = "Дата и время создания задания", example = "2020-12-28 03:47:32", required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления задания", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public TaskDtoResponse() {
    }

    public UserBaseDto getCustomer() {
        return customer;
    }

    public void setCustomer(UserBaseDto customer) {
        this.customer = customer;
    }

    public UserBaseDto getExecutor() {
        return executor;
    }

    public void setExecutor(UserBaseDto executor) {
        this.executor = executor;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
