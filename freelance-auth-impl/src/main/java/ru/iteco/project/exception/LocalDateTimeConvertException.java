package ru.iteco.project.exception;

/**
 * Класс исключения LocalDateTimeConvertException, возникающего при ошибке преобразования
 * значения поля с типом LocalDateTime в значение с типом sql.Timestamp для сохранения в БД
 */
public class LocalDateTimeConvertException extends RuntimeException {


    public LocalDateTimeConvertException() {
        super();
    }

    public LocalDateTimeConvertException(String message) {
        super(message);
    }

    public LocalDateTimeConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocalDateTimeConvertException(Throwable cause) {
        super(cause);
    }

}
