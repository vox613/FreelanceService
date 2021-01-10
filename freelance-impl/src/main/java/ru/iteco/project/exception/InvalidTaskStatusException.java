package ru.iteco.project.exception;

/**
 * Класс исключения InvalidTaskStatusException, возникающего при попытке создания/получения/обновления задания
 * с использованием невалидного/удаленного статуса задания
 */
public class InvalidTaskStatusException extends RuntimeException {


    public InvalidTaskStatusException() {
        super();
    }

    public InvalidTaskStatusException(String message) {
        super(message);
    }

    public InvalidTaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidTaskStatusException(Throwable cause) {
        super(cause);
    }

}
