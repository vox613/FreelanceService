package ru.iteco.project.exception;

import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Исключение, возникающее при ошибках заключения договора,
 * таких как некорректность введенных данных или их неполнота
 */
public class ContractConclusionException extends RuntimeException {

    /*** Список возникших ошибок */
    private List<ObjectError> objectErrorList = new ArrayList<>();

    public ContractConclusionException() {
        super();
    }

    public ContractConclusionException(String message) {
        super(message);
    }

    public ContractConclusionException(String message, List<ObjectError> objectErrorList) {
        super(message);
        this.objectErrorList = objectErrorList;
    }

    public ContractConclusionException(String message, Throwable cause) {
        super(message, cause);
    }


    public List<ObjectError> getObjectErrorList() {
        return objectErrorList;
    }

    public void setObjectErrorList(List<ObjectError> objectErrorList) {
        this.objectErrorList = objectErrorList;
    }
}
