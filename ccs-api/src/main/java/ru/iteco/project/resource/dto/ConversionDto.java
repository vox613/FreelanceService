package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ApiModel(description = "Модель содержащая данные для процесса конвертации, а также результат процесса")
public class ConversionDto implements Serializable {

    @ApiModelProperty(value = "Текстовая аббревиатура валюты из которой происходит конвертация",
            example = "AUD", required = true)
    private String fromCurrency;

    @ApiModelProperty(value = "Текстовая аббревиатура валюты в которую происходит конвертация",
            example = "RUB", required = true)
    private String toCurrency;

    @ApiModelProperty(value = "Сумма преобразования", example = "150", required = true)
    private BigDecimal amount;

    @ApiModelProperty(value = "Конвертированная к целевой валюте сумма", example = "8749,995")
    private BigDecimal convertedAmount;

    @ApiModelProperty(value = "Список ошибок валидации задания", allowEmptyValue = true, hidden = true)
    private List<ObjectError> errors;

    public ConversionDto() {
    }

    public ConversionDto(String fromCurrency, String toCurrency, BigDecimal amount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public List<ObjectError> getErrors() {
        return errors;
    }

    public void setErrors(List<ObjectError> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "ConversionDto{" +
                "fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", amount=" + amount +
                ", convertedAmount=" + convertedAmount +
                ", errors=" + errors +
                '}';
    }
}
