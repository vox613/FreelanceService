package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;

@ApiModel(description = "Данные модели роли пользователя для запроса")
public class ClientRoleDtoRequest extends ClientRoleBaseDto {

    @ApiModelProperty(value = "Список ошибок валидации роли пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
