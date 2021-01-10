package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.UserStatusDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса UserStatusDtoRequest
 */
@Component
public class UserStatusDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(UserStatusDtoRequestValidator.class.getName());


    public UserStatusDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return UserStatusDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserStatusDtoRequest userStatusForm = (UserStatusDtoRequest) target;

        if (ObjectUtils.isEmpty(userStatusForm.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "status.user.id.empty", "userId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userStatusForm.getValue())) {
            logger.error("status value is empty");
            prepareErrorMessage(errors, "status.user.value.empty", "value");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userStatusForm.getDescription())) {
            logger.error("status description is empty");
            prepareErrorMessage(errors, "status.user.description.empty", "description");
        }
    }
}
