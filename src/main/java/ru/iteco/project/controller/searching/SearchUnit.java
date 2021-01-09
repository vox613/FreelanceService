package ru.iteco.project.controller.searching;

import ru.iteco.project.controller.dto.DtoInterface;
import ru.iteco.project.service.specifications.SearchOperations;

/**
 * Класс-контейнер для параметров поиска, относящихся к одному полю сущности
 */
public class SearchUnit implements DtoInterface {

    /**
     * Тип операции поиска
     */
    private SearchOperations searchOperation;

    /**
     * Значение искомого поля
     */
    private String value;

    /**
     * Минимальное значение искомого поля
     */
    private String minValue;

    /**
     * Макимальное значение искомого поля
     */
    private String maxValue;

    public SearchUnit(SearchOperations searchOperation, String value, String minValue, String maxValue) {
        this.searchOperation = searchOperation;
        this.value = value;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public SearchUnit() {
    }


    public SearchOperations getSearchOperation() {
        return searchOperation;
    }

    public void setSearchOperation(SearchOperations searchOperation) {
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
