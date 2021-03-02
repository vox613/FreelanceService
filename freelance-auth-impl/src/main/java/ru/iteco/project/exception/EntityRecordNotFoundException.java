package ru.iteco.project.exception;

/**
 * Класс исключения EntityRecordNotFoundException, возникающего при попытке получения записи, которой нет в БД
 */
public class EntityRecordNotFoundException extends RuntimeException {


    public EntityRecordNotFoundException() {
        super();
    }

    public EntityRecordNotFoundException(String message) {
        super(message);
    }

    public EntityRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityRecordNotFoundException(Throwable cause) {
        super(cause);
    }

}
