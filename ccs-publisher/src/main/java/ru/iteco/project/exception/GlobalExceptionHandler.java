package ru.iteco.project.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.iteco.project.resource.dto.ResponseError;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * Перехватчик исключений для пакета "ru.iteco.project.controller"
 */
@RestControllerAdvice(basePackages = "ru.iteco.project.controller")
public class GlobalExceptionHandler {

    /**
     * Перехватчик исключения LocalDateTimeConvertException, возникающего при ошибке преобразования
     * значения поля с типом LocalDateTime в значение с типом sql.Timestamp для сохранения в БД
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(LocalDateTimeConvertException.class)
    public ResponseEntity<ResponseError> localDateTimeConvertException(LocalDateTimeConvertException e) {
        ResponseError responseError = new ResponseError(UUID.randomUUID(), e.getLocalizedMessage(), e.getClass().getName());
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Класс исключения CurrencyNotExistException, возникающего при попытке конвертации из/в валюту
     * которой не существует
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(CurrencyNotExistException.class)
    public ResponseEntity<ResponseError> currencyNotExistException(CurrencyNotExistException e) {
        ResponseError responseError = new ResponseError(UUID.randomUUID(), e.getLocalizedMessage(), e.getClass().getName());
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    /**
     * Перехватчик всех остальных не предусмотренных Exception, возникающих при работе приложения
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> exception(Exception e) {
        ObjectError objectError = new ObjectError(String.valueOf(e.getCause()), Arrays.toString(e.getStackTrace()));
        ResponseError responseError = new ResponseError(UUID.randomUUID(), e.getLocalizedMessage(),
                e.getClass().getName(), Collections.singletonList(objectError));
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
