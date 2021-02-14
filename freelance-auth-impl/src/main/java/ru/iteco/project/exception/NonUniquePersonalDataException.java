package ru.iteco.project.exception;

/**
 * Класс исключения NonUniquePersonalDataException, возникающего при попытке создания пользователя с уникальными
 * персональными данными, которые уже принадлежат другой учетной записи
 */
public class NonUniquePersonalDataException extends RuntimeException {

    public NonUniquePersonalDataException() {
        super();
    }

    public NonUniquePersonalDataException(String message) {
        super(message);
    }

    public NonUniquePersonalDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniquePersonalDataException(Throwable cause) {
        super(cause);
    }

}
