package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;


@ApiModel(description = "Данные модели пользователя для запроса")
public class ClientDtoRequest extends ClientBaseDto {

    @ApiModelProperty(value = "Список ошибок валидации данных пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public ClientDtoRequest() {
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
