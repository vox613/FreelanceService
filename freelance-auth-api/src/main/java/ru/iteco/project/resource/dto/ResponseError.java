package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.UUID;

@ApiModel(description = "Модель отображения информации об исключениях")
public class ResponseError {

    @ApiModelProperty(value = "Идентификатор ошибки", example = "748b310e-486d-11eb-94e0-0242ac130002", required = true)
    private UUID id;

    @ApiModelProperty(value = "Сообщение ошибки", example = "Невалидный статус контракта!", required = true)
    private String message;

    @ApiModelProperty(value = "Код ошибки", example = "ru.iteco.project.exception.InvalidTaskStatusException",
            required = true)
    private String codeError;

    @ApiModelProperty(value = "Список ошибок",
            example = "\"objectErrorList\": [\n" +
                    "        {\n" +
                    "            \"codes\": [\n" +
                    "                \"contract.confirmation.code.mismatch.contractDtoRequest.repeatConfirmationCode\",\n" +
                    "                \"contract.confirmation.code.mismatch.repeatConfirmationCode\",\n" +
                    "                \"contract.confirmation.code.mismatch.java.lang.String\",\n" +
                    "                \"contract.confirmation.code.mismatch\"\n" +
                    "            ],\n" +
                    "            \"defaultMessage\": \"Confirmation codes don't match\",\n" +
                    "            \"objectName\": \"contractDtoRequest\",\n" +
                    "            \"field\": \"repeatConfirmationCode\",\n" +
                    "            \"rejectedValue\": \"confirmationCoded\",\n" +
                    "            \"bindingFailure\": false,\n" +
                    "            \"code\": \"contract.confirmation.code.mismatch\"\n" +
                    "        }\n" +
                    "    ]",
            allowEmptyValue = true)
    private List<ObjectError> objectErrorList;

    public ResponseError() {
    }

    public ResponseError(UUID id, String message, String codeError) {
        this.id = id;
        this.message = message;
        this.codeError = codeError;
    }

    public ResponseError(UUID id, String message, String codeError, List<ObjectError> objectErrorList) {
        this.id = id;
        this.message = message;
        this.codeError = codeError;
        this.objectErrorList = objectErrorList;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeError() {
        return codeError;
    }

    public void setCodeError(String codeError) {
        this.codeError = codeError;
    }

    public List<ObjectError> getObjectErrorList() {
        return objectErrorList;
    }

    public void setObjectErrorList(List<ObjectError> objectErrorList) {
        this.objectErrorList = objectErrorList;
    }
}
