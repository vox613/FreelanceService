package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

@ApiModel(description = "Базовая модель статуса контракта")
public class ContractStatusBaseDto implements DtoInterface {

    @ApiModelProperty(value = "Идентификатор статуса контракта", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Наименование статуса контракта", example = "PAID", required = true)
    private String value;

    @ApiModelProperty(value = "Описание статуса контракта", example = "Договор оплачен", required = true)
    private String description;


    public ContractStatusBaseDto() {
    }

    public ContractStatusBaseDto(UUID id, String value) {
        this.id = id;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
