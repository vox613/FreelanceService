package ru.iteco.project.exception;

/**
 * Класс исключения InvalidSearchExpressionException, возникающего при некооректном выражени для поиска
 */
public class InvalidSearchExpressionException extends RuntimeException {


    public InvalidSearchExpressionException() {
        super();
    }

    public InvalidSearchExpressionException(String message) {
        super(message);
    }

    public InvalidSearchExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSearchExpressionException(Throwable cause) {
        super(cause);
    }

}
