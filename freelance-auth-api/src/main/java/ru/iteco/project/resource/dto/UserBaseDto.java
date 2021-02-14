package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.UUID;


@ApiModel(description = "Базовая модель пользователя")
public class UserBaseDto implements Serializable {

    @ApiModelProperty(value = "Идентификатор пользователя", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Логин пользователя", example = "login", required = true)
    private String username;

    @ApiModelProperty(value = "Email пользователя", example = "email@mail.com", required = true)
    private String email;

    @ApiModelProperty(value = "Роль пользователя", example = "CUSTOMER", required = true,
            allowableValues = "ADMIN, USER")
    private String role;

    @ApiModelProperty(value = "Статус пользователя", example = "ACTIVE", required = true,
            allowableValues = "ACTIVE, DELETED, BLOCKED")
    private String status;


    public UserBaseDto() {
    }

    public UserBaseDto(UUID id, String username, String email, String role, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
