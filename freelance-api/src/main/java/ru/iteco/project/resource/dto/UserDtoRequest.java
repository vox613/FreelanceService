package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;


@ApiModel(description = "Данные модели пользователя для запроса")
public class UserDtoRequest extends UserBaseDto {

    @ApiModelProperty(value = "Пароль пользователя", example = "123456", required = true)
    private String password;

    @ApiModelProperty(value = "Подтверждение пароля", example = "123456", required = true)
    private String repeatPassword;

    @ApiModelProperty(value = "Список ошибок валидации данных пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public UserDtoRequest() {
    }

    public UserDtoRequest(String password, String repeatPassword) {
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
