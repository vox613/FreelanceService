package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.AuthUserDto;

/**
 * Класс содержит валидаторы для полей объекта запроса AuthUserDto
 */
@Component
public class AuthDtoValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(AuthDtoValidator.class.getName());

    public AuthDtoValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return AuthUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthUserDto authUserDto = (AuthUserDto) target;

        if (ObjectUtils.isEmpty(authUserDto.getUsername())) {
            logger.error("username is empty");
            prepareErrorMessage(errors, "user.username.empty", "username");
        }
        if (errors.hasErrors()) return;

        if (ObjectUtils.isEmpty(authUserDto.getPassword())) {
            logger.error("password is empty");
            prepareErrorMessage(errors, "user.password.empty", "password");
        }
        if (errors.hasErrors()) return;

        if (ObjectUtils.isEmpty(authUserDto.getAudience())) {
            logger.error("audience is empty");
            prepareErrorMessage(errors, "user.audience.empty", "audience");
        }

    }

}
