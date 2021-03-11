package ru.iteco.project.exception;

/**
 * Класс исключения InsufficientFundsException, возникающего при попытке заключения договора
 * при недостатке средств на счету заказчика
 */
public class InsufficientFundsException extends RuntimeException {


    public InsufficientFundsException() {
        super();
    }

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientFundsException(Throwable cause) {
        super(cause);
    }

}
