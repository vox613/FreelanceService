package ru.iteco.project.service.specifications;

import ru.iteco.project.exception.InvalidSearchOperationException;

import java.util.Arrays;

/**
 * Доступные операции объединения ограничений для формирования спецификации
 */
public enum JoinOperations {
    /*** Логическое "и" */
    AND,
    /*** Логическое "или" */
    OR;

    /**
     * Метод получения объекта JoinOperations по его сроковому представлению
     *
     * @param value - строковое представление JoinOperations
     * @return - объект JoinOperations соответствующий переданной строке, или InvalidSearchOperationException,
     * если данного значения не существует
     */
    public static JoinOperations fromString(String value) {
        if (value != null) {
            return Arrays.stream(values())
                    .filter(searchOperations -> searchOperations.name().equals(value))
                    .findFirst()
                    .orElseThrow(() -> new InvalidSearchOperationException("Некорректная операция объединения предикатов!"));
        }
        return null;
    }
}
