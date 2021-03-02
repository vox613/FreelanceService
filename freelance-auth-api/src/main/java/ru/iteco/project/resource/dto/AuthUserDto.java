package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "Базовая модель данных пользователя для аутентификации")
public class AuthUserDto implements Serializable {

    @ApiModelProperty(value = "Имя пользователя", example = "username", required = true)
    private String username;

    @ApiModelProperty(value = "Пароль пользователя", example = "password123456", required = true)
    private String password;

    @ApiModelProperty(value = "Приложение, для которого выдается токен", example = "freelance-service", allowEmptyValue = true)
    private String audience;

    @ApiModelProperty(value = "Список ошибок валидации данных пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;

    public AuthUserDto(String username, String password, String audience) {
        this.username = username;
        this.password = password;
        this.audience = audience;
    }

    public AuthUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
