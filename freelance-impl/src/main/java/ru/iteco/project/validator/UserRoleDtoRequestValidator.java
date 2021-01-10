package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.UserRoleDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса UserRoleDtoRequest
 */
@Component
public class UserRoleDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(UserRoleDtoRequestValidator.class.getName());


    public UserRoleDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return UserRoleDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserRoleDtoRequest userRoleForm = (UserRoleDtoRequest) target;

        if (ObjectUtils.isEmpty(userRoleForm.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "roles.user.id.empty", "userId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userRoleForm.getValue())) {
            logger.error("role value is empty");
            prepareErrorMessage(errors, "roles.value.empty", "value");
        }
    }
}
