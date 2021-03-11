package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;

@ApiModel(description = "Данные модели контракта для запроса")
public class ContractDtoRequest extends ContractBaseDto {

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
