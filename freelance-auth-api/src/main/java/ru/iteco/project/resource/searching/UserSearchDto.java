package ru.iteco.project.resource.searching;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.AbstractSearchDto;
import ru.iteco.project.resource.SearchUnit;

@ApiModel(description = "Модель запроса для поиска пользователей")
public class UserSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Логин пользователя", allowEmptyValue = true)
    private SearchUnit username;

    @ApiModelProperty(value = "Email пользователя", allowEmptyValue = true)
    private SearchUnit email;

    @ApiModelProperty(value = "Роль пользователя", allowEmptyValue = true)
    private SearchUnit role;

    @ApiModelProperty(value = "Статус пользователя", allowEmptyValue = true)
    private SearchUnit status;


    public UserSearchDto() {
    }


    public SearchUnit getUsername() {
        return username;
    }

    public void setUsername(SearchUnit username) {
        this.username = username;
    }

    public SearchUnit getEmail() {
        return email;
    }

    public void setEmail(SearchUnit email) {
        this.email = email;
    }

    public SearchUnit getRole() {
        return role;
    }

    public void setRole(SearchUnit role) {
        this.role = role;
    }

    public SearchUnit getStatus() {
        return status;
    }

    public void setStatus(SearchUnit status) {
        this.status = status;
    }

    @Override
    public UserSearchDto searchData() {
        return this;
    }
}
