package ru.iteco.project.service.specifications;


import ru.iteco.project.exception.InvalidSearchOperationException;

import java.util.Arrays;


/**
 * Доступные типы операций ограничений для формирования спецификации
 */

public enum SearchOperations {

    BETWEEN {
        public SearchOperations negated() {
            return NOT_BETWEEN;
        }
    },
    NOT_BETWEEN {
        public SearchOperations negated() {
            return BETWEEN;
        }
    },
    LIKE {
        public SearchOperations negated() {
            return NOT_LIKE;
        }
    },
    NOT_LIKE {
        public SearchOperations negated() {
            return LIKE;
        }
    },
    EQUAL {
        public SearchOperations negated() {
            return NOT_EQUAL;
        }
    },
    NOT_EQUAL {
        public SearchOperations negated() {
            return EQUAL;
        }
    },
    LESS_THAN {
        public SearchOperations negated() {
            return GREATER_THAN_OR_EQUAL;
        }
    },
    LESS_THAN_OR_EQUAL {
        public SearchOperations negated() {
            return GREATER_THAN;
        }
    },
    GREATER_THAN {
        public SearchOperations negated() {
            return LESS_THAN_OR_EQUAL;
        }
    },
    GREATER_THAN_OR_EQUAL {
        public SearchOperations negated() {
            return LESS_THAN;
        }
    };

    public abstract SearchOperations negated();

    /**
     * Метод получения объекта SearchOperations по его сроковому представлению
     *
     * @param value - строковое представление SearchOperations
     * @return - объект SearchOperations соответствующий переданной строке, или InvalidSearchOperationException,
     * если данного значения не существует
     */
    public static SearchOperations fromString(String value) {
        if (value != null) {
            return Arrays.stream(values())
                    .filter(searchOperations -> searchOperations.name().equals(value))
                    .findFirst()
                    .orElseThrow(() -> new InvalidSearchOperationException("Некорректная операция для формирования предиката!"));
        }
        return null;
    }

}