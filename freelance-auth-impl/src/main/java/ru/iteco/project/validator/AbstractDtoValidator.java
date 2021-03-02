package ru.iteco.project.validator;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import java.util.Locale;

/**
 * Абстрактный класс для DTO валидаторов
 */
public abstract class AbstractDtoValidator {

    /**
     * Объект-источник текстовок для ошибок
     */
    private final MessageSource messageSource;


    public AbstractDtoValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    /**
     * Метод подготавливает сообщение об ошибке и устанавливает его в общий список
     *
     * @param errors    - объект ошибок
     * @param errCode   - код ошибки
     * @param fieldName - имя поля при проверке которого возникла ошибка
     */
    protected void prepareErrorMessage(Errors errors, String errCode, String fieldName) {
        String message = messageSource.getMessage(errCode, new Object[]{}, Locale.getDefault());
        errors.rejectValue(fieldName, errCode, message);
    }
}
