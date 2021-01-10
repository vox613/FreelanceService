package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;


@ApiModel(description = "Базовая модель роли пользователя")
public class UserRoleBaseDto implements DtoInterface {

    @ApiModelProperty(value = "Идентификатор роли пользователя", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Наименование роли пользователя", example = "CUSTOMER", required = true)
    private String value;


    public UserRoleBaseDto() {
    }

    public UserRoleBaseDto(UUID id, String value) {
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
}
