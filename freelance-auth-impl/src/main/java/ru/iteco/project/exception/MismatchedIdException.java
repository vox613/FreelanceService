package ru.iteco.project.exception;

/**
 * Исключение, возникающее при несовпадении id сущности в pathVariable ив теле запроса - requestBody
 */
public class MismatchedIdException extends RuntimeException {

    public MismatchedIdException() {
        super();
    }

    public MismatchedIdException(String message) {
        super(message);
    }

    public MismatchedIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public MismatchedIdException(Throwable cause) {
        super(cause);
    }

}
