package ru.iteco.project.exception;

/**
 * Исключение, возникающее при пустого сообщения в качестве подтверждения успешной передачи отчета
 */
public class EmptyAckMessageException extends RuntimeException {

    public EmptyAckMessageException() {
        super();
    }

    public EmptyAckMessageException(String message) {
        super(message);
    }

    public EmptyAckMessageException(String message, Throwable cause) {
        super(message, cause);
    }

}
