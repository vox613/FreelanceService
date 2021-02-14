package ru.iteco.project.exception;

/**
 * Класс исключения UnavailableRoleOperationException, возникающего при попытке совершения недопустимой операции
 */
public class UnavailableOperationException extends RuntimeException {


    public UnavailableOperationException() {
        super();
    }

    public UnavailableOperationException(String message) {
        super(message);
    }

    public UnavailableOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableOperationException(Throwable cause) {
        super(cause);
    }

}
