package ru.iteco.project.exception;

/**
 * Класс исключения UnavailableRoleOperationException, возникающего при попытке совершения недопустимой операции
 * для текущей роли пользователя
 */
public class UnavailableRoleOperationException extends RuntimeException {


    public UnavailableRoleOperationException() {
        super();
    }

    public UnavailableRoleOperationException(String message) {
        super(message);
    }

    public UnavailableRoleOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableRoleOperationException(Throwable cause) {
        super(cause);
    }

}
