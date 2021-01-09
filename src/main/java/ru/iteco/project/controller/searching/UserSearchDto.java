package ru.iteco.project.controller.searching;


/**
 * Класс - dto для поиска пользователей с переданными ограничениями по полям
 */
public class UserSearchDto extends AbstractSearchDto {

    /*** Имя пользователя */
    private SearchUnit firstName;

    /*** Фамилия пользователя */
    private SearchUnit secondName;

    /*** Отчество пользователя */
    private SearchUnit lastName;

    /*** Логин пользователя */
    private SearchUnit login;

    /*** Email пользователя */
    private SearchUnit email;

    /*** Номер телефона пользователя */
    private SearchUnit phoneNumber;

    /*** Роль пользователя */
    private SearchUnit role;

    /*** Статус пользователя */
    private SearchUnit userStatus;

    /*** Кошелек пользователя */
    private SearchUnit wallet;

    /*** Дата и время создания пользователя */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления пользователя */
    private SearchUnit updatedAt;


    public UserSearchDto() {
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

    public SearchUnit getLogin() {
        return login;
    }

    public void setLogin(SearchUnit login) {
        this.login = login;
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

    public SearchUnit getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(SearchUnit userStatus) {
        this.userStatus = userStatus;
    }

    public SearchUnit getWallet() {
        return wallet;
    }

    public void setWallet(SearchUnit wallet) {
        this.wallet = wallet;
    }

    public SearchUnit getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(SearchUnit createdAt) {
        this.createdAt = createdAt;
    }

    public SearchUnit getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(SearchUnit updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public UserSearchDto searchData() {
        return this;
    }
}
