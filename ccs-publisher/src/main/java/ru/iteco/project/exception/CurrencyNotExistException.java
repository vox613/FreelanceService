package ru.iteco.project.exception;

/**
 * Класс исключения CurrencyNotExistException, возникающего при попытке конвертации из/в валюту которой не существует
 */
public class CurrencyNotExistException extends RuntimeException {

    public CurrencyNotExistException() {
        super();
    }

    public CurrencyNotExistException(String message) {
        super(message);
    }

    public CurrencyNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyNotExistException(Throwable cause) {
        super(cause);
    }

}
