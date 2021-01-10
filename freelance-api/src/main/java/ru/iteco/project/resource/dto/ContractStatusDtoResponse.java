package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Данные модели статуса контракта для ответа")
public class ContractStatusDtoResponse extends ContractStatusBaseDto {

    @ApiModelProperty(value = "Дата и время создания статуса контракта", example = "2020-12-28 03:47:32",
            required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления статуса контракта", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public ContractStatusDtoResponse() {
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
