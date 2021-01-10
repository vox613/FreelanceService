package ru.iteco.project.exception;

/**
 * Класс исключения InvalidContractStatusException, возникающего при попытке обновления контракта
 * с использованием невалидного/удаленного статуса контракта
 */
public class InvalidContractStatusException extends RuntimeException {


    public InvalidContractStatusException() {
        super();
    }

    public InvalidContractStatusException(String message) {
        super(message);
    }

    public InvalidContractStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidContractStatusException(Throwable cause) {
        super(cause);
    }

}
