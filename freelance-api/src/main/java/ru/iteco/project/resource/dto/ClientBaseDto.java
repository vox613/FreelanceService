package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.UUID;


@ApiModel(description = "Базовая модель пользователя")
public class ClientBaseDto implements DtoInterface {

    @ApiModelProperty(value = "Идентификатор пользователя", example = "748b310e-486d-11eb-94e0-0242ac130002",
            allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Имя пользователя", example = "firstName", required = true)
    private String firstName;

    @ApiModelProperty(value = "Отчество пользователя", example = "secondName", allowEmptyValue = true)
    private String secondName;

    @ApiModelProperty(value = "Фамилия пользователя", example = "lastName", required = true)
    private String lastName;

    @ApiModelProperty(value = "Email пользователя", example = "email@mail.com", required = true)
    private String email;

    @ApiModelProperty(value = "Номер телефона пользователя", example = "81234567898", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "Роль пользователя", example = "CUSTOMER", required = true,
            allowableValues = "ADMIN, CUSTOMER, EXECUTOR")
    private String clientRole;

    @ApiModelProperty(value = "Статус пользователя", example = "ACTIVE", required = true,
            allowableValues = "NOT_EXIST, CREATED, BLOCKED, ACTIVE")
    private String clientStatus;

    @ApiModelProperty(value = "Кошелек пользователя", example = "1500", required = true)
    private BigDecimal wallet = new BigDecimal(0);


    public ClientBaseDto() {
    }

    public ClientBaseDto(UUID id, String firstName, String secondName, String lastName, String email, String phoneNumber,
                         String clientRole, String clientStatus, BigDecimal wallet) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.clientRole = clientRole;
        this.clientStatus = clientStatus;
        this.wallet = wallet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientRole() {
        return clientRole;
    }

    public void setClientRole(String clientRole) {
        this.clientRole = clientRole;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }
}
