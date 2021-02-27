package ru.iteco.project.exception;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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

    private final String SYSTEM_ID_KEY = "spring.application.name";

    /*** Экземпляр окружения*/
    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }


    /**
     * Перехватчик исключения MismatchedIdException, возникающего при несовпадении id сущности в pathVariable и
     * в теле запроса - requestBody
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(MismatchedIdException.class)
    public ResponseEntity<ResponseError> mismatchedIdException(MismatchedIdException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Перехватчик исключения InvalidUserRoleException, возникающего при попытке создать или получить из БД
     * пользователя с невалидной ролью
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(InvalidUserRoleException.class)
    public ResponseEntity<ResponseError> invalidUserRoleException(InvalidUserRoleException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Перехватчик исключения InvalidUserStatusException, возникающего при попытке создать пользователя с невалидным
     * статусом
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(InvalidUserStatusException.class)
    public ResponseEntity<ResponseError> invalidUserStatusException(InvalidUserStatusException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Перехватчик исключения LocalDateTimeConvertException, возникающего при ошибке преобразования
     * значения поля с типом LocalDateTime в значение с типом sql.Timestamp для сохранения в БД
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(LocalDateTimeConvertException.class)
    public ResponseEntity<ResponseError> localDateTimeConvertException(LocalDateTimeConvertException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Класс исключения InvalidSearchOperationException, возникающего при передаче некорректной операции для поиска
     * или объединения предикатов поиска
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(InvalidSearchOperationException.class)
    public ResponseEntity<ResponseError> invalidSearchOperationException(InvalidSearchOperationException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Класс исключения EntityRecordNotFoundException, возникающего при попытке получения записи, которой нет в БД
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(EntityRecordNotFoundException.class)
    public ResponseEntity<ResponseError> userNotFoundException(EntityRecordNotFoundException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                environment.getProperty(e.getMessage(), "Entity with id not found!"),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Класс исключения NonUniquePersonalDataException, возникающего при попытке создания пользователя с уникальными
     * * персональными данными, которые уже принадлежат другой учетной записи
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(NonUniquePersonalDataException.class)
    public ResponseEntity<ResponseError> userAlreadyExistException(NonUniquePersonalDataException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                environment.getProperty(e.getMessage(), "Already exist!"),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.CONFLICT);
    }


    /**
     * Класс исключения UnavailableOperationException, возникающего при попытке совершения недопустимой операции
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(UnavailableOperationException.class)
    public ResponseEntity<ResponseError> unavailableOperationException(UnavailableOperationException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                environment.getProperty(e.getMessage(), "Unavailable operation!"),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Класс исключения AuthenticationCredentialsNotFoundException, возникающего при
     *
     * @param e - объект исключения
     * @return - объект ResponseError с полной информацией о возникшей проблеме
     */
    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ResponseError> authenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseError> accessDeniedException(AccessDeniedException e) {
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY));
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.FORBIDDEN);
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
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                e.getLocalizedMessage(),
                e.getClass().getName(),
                environment.getProperty(SYSTEM_ID_KEY),
                Collections.singletonList(objectError)
        );
        return new ResponseEntity<>(responseError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
