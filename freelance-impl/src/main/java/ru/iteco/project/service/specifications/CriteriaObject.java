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

    public CriteriaObject(String joinOperation, List<RestrictionValues> restrictions) {
        this.joinOperation = JoinOperations.fromString(joinOperation);
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
     */
    public static class RestrictionValues {
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

        /*** Значение поля для поиска */
        private Object typedValue;

        private RestrictionValues() {
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

        public Object getTypedValue() {
            return typedValue;
        }

        public void setTypedValue(Object typedValue) {
            this.typedValue = typedValue;
        }

        public static RestrictionValues.Builder newBuilder() {
            return new RestrictionValues().new Builder();
        }

        public class Builder {

            private Builder() {
            }

            public Builder setKey(String key) {
                RestrictionValues.this.key = key;
                return this;
            }

            public Builder setSearchOperation(String searchOperation) {
                RestrictionValues.this.searchOperation = SearchOperations.fromString(searchOperation);
                return this;
            }

            public Builder setSearchOperation(SearchOperations searchOperation) {
                RestrictionValues.this.searchOperation = searchOperation;
                return this;
            }

            public Builder setMinValue(String minValue) {
                RestrictionValues.this.minValue = minValue;
                return this;
            }

            public Builder setMaxValue(String maxValue) {
                RestrictionValues.this.maxValue = maxValue;
                return this;
            }

            public Builder setValue(String value) {
                RestrictionValues.this.value = value;
                return this;
            }

            public Builder setTypedValue(Object typedValue) {
                RestrictionValues.this.typedValue = typedValue;
                return this;
            }

            public RestrictionValues build() {
                return RestrictionValues.this;
            }
        }
    }
}
