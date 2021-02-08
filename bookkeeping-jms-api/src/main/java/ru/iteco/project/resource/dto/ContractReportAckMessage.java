package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.UUID;

@ApiModel(description = "Модель сообщения подтверждения корректного получения сообщения")
public class ContractReportAckMessage implements Serializable {

    @ApiModelProperty(value = "Идентификатор контракта/бухгалтерского отчета",
            example = "748b310e-486d-11eb-94e0-0242ac130002", required = true)
    private UUID contractReportId;

    @ApiModelProperty(value = "Статус успешности ответа о получении данных отчета", example = "true", required = true)
    private Boolean ackStatus;

    public ContractReportAckMessage(UUID messageId, Boolean ackStatus) {
        this.contractReportId = messageId;
        this.ackStatus = ackStatus;
    }

    public ContractReportAckMessage() {
    }

    public UUID getContractReportId() {
        return contractReportId;
    }

    public void setContractReportId(UUID contractReportId) {
        this.contractReportId = contractReportId;
    }

    public Boolean getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(Boolean ackStatus) {
        this.ackStatus = ackStatus;
    }
}
