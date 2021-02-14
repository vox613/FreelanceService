package ru.iteco.project.resource.searching;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.AbstractSearchDto;
import ru.iteco.project.resource.SearchUnit;

@ApiModel(description = "Модель запроса для поиска пользователей")
public class ClientSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Имя пользователя", allowEmptyValue = true)
    private SearchUnit firstName;

    @ApiModelProperty(value = "Фамилия пользователя", allowEmptyValue = true)
    private SearchUnit secondName;

    @ApiModelProperty(value = "Отчество пользователя", allowEmptyValue = true)
    private SearchUnit lastName;

    @ApiModelProperty(value = "Email пользователя", allowEmptyValue = true)
    private SearchUnit email;

    @ApiModelProperty(value = "Номер телефона пользователя", allowEmptyValue = true)
    private SearchUnit phoneNumber;

    @ApiModelProperty(value = "Роль пользователя", allowEmptyValue = true)
    private SearchUnit role;

    @ApiModelProperty(value = "Статус пользователя", allowEmptyValue = true)
    private SearchUnit clientStatus;

    @ApiModelProperty(value = "Кошелек пользователя", allowEmptyValue = true)
    private SearchUnit wallet;


    public ClientSearchDto() {
    }

    public SearchUnit getFirstName() {
        return firstName;
    }

    public void setFirstName(SearchUnit firstName) {
        this.firstName = firstName;
    }

    public SearchUnit getSecondName() {
        return secondName;
    }

    public void setSecondName(SearchUnit secondName) {
        this.secondName = secondName;
    }

    public SearchUnit getLastName() {
        return lastName;
    }

    public void setLastName(SearchUnit lastName) {
        this.lastName = lastName;
    }

    public SearchUnit getEmail() {
        return email;
    }

    public void setEmail(SearchUnit email) {
        this.email = email;
    }

    public SearchUnit getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(SearchUnit phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public SearchUnit getRole() {
        return role;
    }

    public void setRole(SearchUnit role) {
        this.role = role;
    }

    public SearchUnit getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(SearchUnit clientStatus) {
        this.clientStatus = clientStatus;
    }

    public SearchUnit getWallet() {
        return wallet;
    }

    public void setWallet(SearchUnit wallet) {
        this.wallet = wallet;
    }

    @Override
    public ClientSearchDto searchData() {
        return this;
    }
}
