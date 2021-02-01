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

    @ApiModelProperty(value = "Сообщение ошибки", example = "Сумма преобразования отсутствует!", required = true)
    private String message;

    @ApiModelProperty(value = "Код ошибки", example = "ru.iteco.project.resource.dto.ConversionDto",
            required = true)
    private String codeError;

    @ApiModelProperty(value = "Список ошибок",
            example = "\"errors\": [\n" +
                    "        {\n" +
                    "            \"codes\": [\n" +
                    "                \"currency.amount.empty.ru.iteco.project.resource.dto.ConversionDto.amount\",\n" +
                    "                \"currency.amount.empty.amount\",\n" +
                    "                \"currency.amount.empty.java.math.BigDecimal\",\n" +
                    "                \"currency.amount.empty\"\n" +
                    "            ],\n" +
                    "            \"arguments\": null,\n" +
                    "            \"defaultMessage\": \"Сумма преобразования отсутствует\",\n" +
                    "            \"objectName\": \"ru.iteco.project.resource.dto.ConversionDto\",\n" +
                    "            \"field\": \"amount\",\n" +
                    "            \"rejectedValue\": null,\n" +
                    "            \"bindingFailure\": false,\n" +
                    "            \"code\": \"currency.amount.empty\"\n" +
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
