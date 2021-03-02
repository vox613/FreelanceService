package ru.iteco.project.exception;

/**
 * Класс исключения InvalidClientStatusException, возникающего при попытке создать пользователя с невалидным
 * статусом
 */
public class InvalidClientStatusException extends RuntimeException {


    public InvalidClientStatusException() {
        super();
    }

    public InvalidClientStatusException(String message) {
        super(message);
    }

    public InvalidClientStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClientStatusException(Throwable cause) {
        super(cause);
    }

}
