package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.ConversionDto;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Класс содержит валидаторы для полей объекта запроса ConversionDto
 */
@Component
@PropertySource(value = "classpath:errors.properties")
public class ConversionDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ConversionDtoValidator.class.getName());

    /*** Объект-источник текстовок для ошибок*/
    private final MessageSource messageSource;

    public ConversionDtoValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ConversionDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ConversionDto conversionDto = (ConversionDto) target;

        if (ObjectUtils.isEmpty(conversionDto.getFromCurrency())) {
            logger.error("FromCurrency is empty");
            prepareErrorMessage(errors, "currency.from.empty", "fromCurrency");
        }
        if (errors.hasErrors()) return;

        if (ObjectUtils.isEmpty(conversionDto.getToCurrency())) {
            logger.error("ToCurrency is empty");
            prepareErrorMessage(errors, "currency.to.empty", "toCurrency");
        }
        if (errors.hasErrors()) return;

        BigDecimal amount = conversionDto.getAmount();
        if (ObjectUtils.isEmpty(amount)) {
            logger.error("amount is empty");
            prepareErrorMessage(errors, "currency.amount.empty", "amount");
        } else if (amount.signum() != 1) {
            logger.error("Incorrect conversion sum!");
            prepareErrorMessage(errors, "currency.amount.incorrect", "amount");
        }

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
