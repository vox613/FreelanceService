package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Данные модели контракта для ответа")
public class ContractDtoResponse extends ContractBaseDto {

    @ApiModelProperty(value = "Модель заказчика (в формате ClientBaseDto)", required = true)
    private ClientBaseDto customer;

    @ApiModelProperty(value = "Модель исполнителя (в формате ClientBaseDto)", required = true)
    private ClientBaseDto executor;

    @ApiModelProperty(value = "Модель задания (в формате TaskBaseDto)", required = true)
    private TaskBaseDto task;

    @ApiModelProperty(value = "Дата и время создания контракта", example = "2020-12-28 03:47:32", required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления контракта", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public ContractDtoResponse() {
    }

    public ClientBaseDto getCustomer() {
        return customer;
    }

    public void setCustomer(ClientBaseDto customer) {
        this.customer = customer;
    }

    public ClientBaseDto getExecutor() {
        return executor;
    }

    public void setExecutor(ClientBaseDto executor) {
        this.executor = executor;
    }

    public TaskBaseDto getTask() {
        return task;
    }

    public void setTask(TaskBaseDto task) {
        this.task = task;
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
