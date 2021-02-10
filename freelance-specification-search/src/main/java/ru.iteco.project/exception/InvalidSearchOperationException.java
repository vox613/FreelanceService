package ru.iteco.project.exception;

/**
 * Класс исключения InvalidSearchOperationException, возникающего при передаче некорректной операции для поиска
 * или объединения предикатов поиска
 */
public class InvalidSearchOperationException extends RuntimeException {


    public InvalidSearchOperationException() {
        super();
    }

    public InvalidSearchOperationException(String message) {
        super(message);
    }

    public InvalidSearchOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSearchOperationException(Throwable cause) {
        super(cause);
    }

}
