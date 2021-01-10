package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

@ApiModel(description = "Базовая модель контракта")
public class ContractBaseDto implements DtoInterface {

    @ApiModelProperty(value = "Идентификатор контракта", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Идентификатор исполнителя", example = "bf51c162-95f3-4e69-ab6d-7ff214430ba6",
            required = true)
    private UUID executorId;

    @ApiModelProperty(value = "Идентификатор задания", example = "82174564-0181-46ac-ae27-7fd4b42c70d5",
            required = true)
    private UUID taskId;

    @ApiModelProperty(value = "Статус контракта", example = "PAID", required = true,
            allowableValues = "PAID, TERMINATED, DONE, ON_FIX")
    private String contractStatus;


    public ContractBaseDto() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getExecutorId() {
        return executorId;
    }

    public void setExecutorId(UUID executorId) {
        this.executorId = executorId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }
}