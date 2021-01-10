package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.dto.DtoInterface;


@ApiModel(description = "Модель-контейнер для параметров поиска, относящихся к одному полю сущности")
public class SearchUnit implements DtoInterface {

    @ApiModelProperty(value = "Тип операции поиска для предиката", example = "LIKE",
            allowableValues = "BETWEEN, NOT_BETWEEN, LIKE, NOT_LIKE, EQUAL, NOT_EQUAL, " +
                    "LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL")
    private String searchOperation;

    @ApiModelProperty(value = "Значение искомого поля", example = "500", allowEmptyValue = true)
    private String value;


    @ApiModelProperty(value = "Минимальное значение искомого поля", example = "100", allowEmptyValue = true)
    private String minValue;


    @ApiModelProperty(value = "Макимальное значение искомого поля", example = "1000", allowEmptyValue = true)
    private String maxValue;

    public SearchUnit(String searchOperation, String value, String minValue, String maxValue) {
        this.searchOperation = searchOperation;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public SearchUnit() {
    }


    public String getSearchOperation() {
        return searchOperation;
    }

    public void setSearchOperation(String searchOperation) {
        this.searchOperation = searchOperation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }
}
