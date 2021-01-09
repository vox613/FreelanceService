package ru.iteco.project.service.specifications;

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

}