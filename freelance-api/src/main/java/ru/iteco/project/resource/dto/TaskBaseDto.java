package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.UUID;

@ApiModel(description = "Базовая модель задания")
public class TaskBaseDto implements DtoInterface {

    @ApiModelProperty(value = "Идентификатор задания", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Идентификатор заказчика", example = "748b310e-486d-11eb-94e0-0242ac130002",
            required = true)
    private UUID customerId;

    @ApiModelProperty(value = "Идентификатор исполнителя", example = "bf51c162-95f3-4e69-ab6d-7ff214430ba6")
    private UUID executorId;

    @ApiModelProperty(value = "Название задания", example = "Задание...", required = true)
    private String title;

    @ApiModelProperty(value = "Описание задания", example = "Описание задания", required = true)
    private String description;

    @ApiModelProperty(value = "Статус задания", example = "REGISTERED", required = true,
            allowableValues = "REGISTERED, IN_PROGRESS, ON_CHECK, ON_FIX, DONE, CANCELED")
    private String taskStatus;

    @ApiModelProperty(value = "Крайние дата и время выполнения задания", example = "2020-12-28 03:47:32", required = true)
    private String taskCompletionDate;

    @ApiModelProperty(value = "Стоимость исполнения задания", example = "1000", required = true)
    private BigDecimal price;


    public TaskBaseDto() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(String taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public UUID getExecutorId() {
        return executorId;
    }

    public void setExecutorId(UUID executorId) {
        this.executorId = executorId;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
