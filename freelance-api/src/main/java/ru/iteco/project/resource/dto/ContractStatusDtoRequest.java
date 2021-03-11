package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;

@ApiModel(description = "Данные модели статуса контракта для запроса")
public class ContractStatusDtoRequest extends ContractStatusBaseDto {


    @ApiModelProperty(value = "Список ошибок валидации статуса контракта", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
