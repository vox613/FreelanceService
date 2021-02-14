package ru.iteco.project.exception;

/**
 * Класс исключения InvalidUserRoleException, возникающего при попытке создать или получить из БД
 * пользователя с невалидной ролью
 */
public class InvalidUserRoleException extends RuntimeException {


    public InvalidUserRoleException() {
        super();
    }

    public InvalidUserRoleException(String message) {
        super(message);
    }

    public InvalidUserRoleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserRoleException(Throwable cause) {
        super(cause);
    }

}
