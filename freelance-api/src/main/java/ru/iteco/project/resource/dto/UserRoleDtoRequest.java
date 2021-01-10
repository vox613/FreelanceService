package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

@ApiModel(description = "Данные модели роли пользователя для запроса")
public class UserRoleDtoRequest extends UserRoleBaseDto {

    @ApiModelProperty(value = "Идентификатор пользователя совершающего действие",
            example = "748b310e-486d-11eb-94e0-0242ac130002",
            required = true)
    private UUID userId;

    @ApiModelProperty(value = "Список ошибок валидации роли пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
