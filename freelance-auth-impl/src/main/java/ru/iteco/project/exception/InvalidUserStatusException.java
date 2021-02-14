package ru.iteco.project.exception;

/**
 * Класс исключения InvalidUserStatusException, возникающего при попытке создать пользователя с невалидным
 * статусом
 */
public class InvalidUserStatusException extends RuntimeException {


    public InvalidUserStatusException() {
        super();
    }

    public InvalidUserStatusException(String message) {
        super(message);
    }

    public InvalidUserStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserStatusException(Throwable cause) {
        super(cause);
    }

}
