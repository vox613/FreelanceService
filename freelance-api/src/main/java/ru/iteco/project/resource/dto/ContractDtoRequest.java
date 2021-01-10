package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

@ApiModel(description = "Данные модели контракта для запроса")
public class ContractDtoRequest extends ContractBaseDto {

    @ApiModelProperty(value = "Идентификатор пользователя совершающего действие",
            example = "748b310e-486d-11eb-94e0-0242ac130002",
            required = true)
    private UUID userId;

    @ApiModelProperty(value = "Код подтверждения оформления контракта", example = "confirmationCode", required = true)
    private String confirmationCode;

    @ApiModelProperty(value = "Повтор кода подтверждения оформления контракта", example = "confirmationCode",
            required = true)
    private String repeatConfirmationCode;

    @ApiModelProperty(value = "Список ошибок валидации контракта", allowEmptyValue = true,
            hidden = true)
    private List<ObjectError> errors;


    public ContractDtoRequest() {
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getRepeatConfirmationCode() {
        return repeatConfirmationCode;
    }

    public void setRepeatConfirmationCode(String repeatConfirmationCode) {
        this.repeatConfirmationCode = repeatConfirmationCode;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }
}
