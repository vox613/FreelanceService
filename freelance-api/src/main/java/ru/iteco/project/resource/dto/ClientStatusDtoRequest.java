package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;


@ApiModel(description = "Данные модели статуса пользователя для запроса")
public class ClientStatusDtoRequest extends ClientStatusBaseDto {

    @ApiModelProperty(value = "Идентификатор клиента совершающего действие",
            example = "748b310e-486d-11eb-94e0-0242ac130002",
            required = true)
    private UUID clientId;

    @ApiModelProperty(value = "Список ошибок валидации статуса пользователя", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}