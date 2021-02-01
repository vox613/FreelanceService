package ru.iteco.project.exception;

import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Исключение, возникающее при ошибках взаимодействия с сервисом конвертации валюты
 */
public class CurrencyConverterException extends RuntimeException {

    /*** Список возникших ошибок */
    private List<ObjectError> objectErrorList = new ArrayList<>();

    public CurrencyConverterException() {
        super();
    }

    public CurrencyConverterException(String message) {
        super(message);
    }

    public CurrencyConverterException(String message, List<ObjectError> objectErrorList) {
        super(message);
        this.objectErrorList = objectErrorList;
    }

    public CurrencyConverterException(String message, Throwable cause) {
        super(message, cause);
    }


    public List<ObjectError> getObjectErrorList() {
        return objectErrorList;
    }

    public void setObjectErrorList(List<ObjectError> objectErrorList) {
        this.objectErrorList = objectErrorList;
    }
}
