package ru.iteco.project.exception;

/**
 * Класс исключения InvalidClientRoleException, возникающего при попытке создать или получить из БД
 * пользователя с невалидной ролью
 */
public class InvalidClientRoleException extends RuntimeException {


    public InvalidClientRoleException() {
        super();
    }

    public InvalidClientRoleException(String message) {
        super(message);
    }

    public InvalidClientRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClientRoleException(Throwable cause) {
        super(cause);
    }

}
