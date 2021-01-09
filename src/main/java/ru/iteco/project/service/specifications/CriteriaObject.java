package ru.iteco.project.service.specifications;

import java.util.List;

/**
 * Объект - контейнер для критериеф поиска и ограничений
 */
public class CriteriaObject {

    /*** Тип операции объединения ограничений */
    private JoinOperations joinOperation;

    /*** Список ограничений поиска */
    private List<RestrictionValues> restrictions;


    public CriteriaObject(JoinOperations joinOperation, List<RestrictionValues> restrictions) {
        this.joinOperation = joinOperation;
        this.restrictions = restrictions;
    }

    public CriteriaObject() {
    }

    public JoinOperations getJoinOperation() {
        return joinOperation;
    }

    public void setJoinOperation(JoinOperations joinOperation) {
        this.joinOperation = joinOperation;
    }

    public List<RestrictionValues> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<RestrictionValues> restrictions) {
        this.restrictions = restrictions;
    }


    /**
     * Класс-контейнер для данных ограничей поиска
     *
     * @param <T> - тип данных для поиска
     */
    public static class RestrictionValues<T> {
        /*** Ключ - наименование поля для которого создается ограничение */
        private String key;
        /*** Тип операции поиска */
        private SearchOperations searchOperation;
        /*** Минимальное значение поля (для поиска по диапазонам) */
        private String minValue;
        /*** Максимальное значение поля (для поиска по диапазонам) */
        private String maxValue;
        /*** Значение поля для поиска */
        private String value;
        /*** Типизированное значение поля для поиска */
        private T typedValue;


        public RestrictionValues(String key, SearchOperations searchOperation, String minValue, String maxValue, String value, T typedValue) {
            this.key = key;
            this.searchOperation = searchOperation;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.value = value;
            this.typedValue = typedValue;
        }

        public RestrictionValues(String key, SearchOperations searchOperation, String value, String minValue, String maxValue) {
            this.key = key;
            this.searchOperation = searchOperation;
            this.value = value;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public RestrictionValues(String key, SearchOperations searchOperation, String value, T typedValue) {
            this.key = key;
            this.searchOperation = searchOperation;
            this.value = value;
            this.typedValue = typedValue;
        }

        public RestrictionValues(String key, SearchOperations searchOperation, T typedValue) {
            this.key = key;
            this.searchOperation = searchOperation;
            this.typedValue = typedValue;
        }

        public RestrictionValues(String key, String value, SearchOperations searchOperation) {
            this.key = key;
            this.searchOperation = searchOperation;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public SearchOperations getSearchOperation() {
            return searchOperation;
        }

        public void setSearchOperation(SearchOperations searchOperation) {
            this.searchOperation = searchOperation;
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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public T getTypedValue() {
            return typedValue;
        }

        public void setTypedValue(T typedValue) {
            this.typedValue = typedValue;
        }
    }
}
