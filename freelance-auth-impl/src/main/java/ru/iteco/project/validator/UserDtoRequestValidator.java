package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.resource.dto.UserDtoRequest;

import java.util.ArrayList;

/**
 * Класс содержит валидаторы для полей объекта запроса UserDtoRequest
 */
@Component
@PropertySource(value = {"classpath:application.yml"})
public class UserDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(UserDtoRequestValidator.class.getName());

    @Value("${user.email.regexp}")
    private String emailRegExpValidator;

    @Value("${user.password.length.min}")
    private Integer minUserPasswordLength;

    public UserDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return UserDtoRequest.class.equals(clazz) || ArrayList.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof UserDtoRequest) {
            UserDtoRequest userForm = (UserDtoRequest) target;
            performUserDtoRequestChecks(userForm, errors);
        }
        if (target instanceof ArrayList) {
            ArrayList<UserDtoRequest> userFormsList = (ArrayList<UserDtoRequest>) target;
            performUserDtoRequestListChecks(userFormsList, errors);
        }
    }

    private void performUserDtoRequestListChecks(ArrayList<UserDtoRequest> userFormsList, Errors errors) {
        userFormsList.forEach(userDtoRequest -> performUserDtoRequestChecks(userDtoRequest, errors));
    }

    private void performUserDtoRequestChecks(UserDtoRequest userForm, Errors errors) {

        if (ObjectUtils.isEmpty(userForm.getUsername())) {
            logger.error("username is empty");
            prepareErrorMessage(errors, "user.username.empty", "username");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userForm.getEmail())) {
            logger.error("email is empty");
            prepareErrorMessage(errors, "user.email.empty", "email");
        } else if (!userForm.getEmail().matches(emailRegExpValidator)) {
            logger.error("email is incorrect");
            prepareErrorMessage(errors, "user.email.incorrect", "email");
        }
        if (errors.hasErrors()) return;


        String role = userForm.getRole();
        if (ObjectUtils.isEmpty(role)) {
            logger.error("role is empty");
            prepareErrorMessage(errors, "user.role.empty", "role");
        } else if (!UserRole.isCorrectValue(role)) {
            logger.error("role is incorrect");
            prepareErrorMessage(errors, "user.role.incorrect", "role");
        }
        if (errors.hasErrors()) return;


        String status = userForm.getStatus();
        if (ObjectUtils.isEmpty(status)) {
            logger.error("user status empty");
            prepareErrorMessage(errors, "user.status.empty", "status");
        } else if (!UserStatus.isCorrectValue(status)) {
            logger.error("status is incorrect");
            prepareErrorMessage(errors, "user.status.incorrect", "status");
        }
        if (errors.hasErrors()) return;


        String password = userForm.getPassword();
        String repeatPassword = userForm.getRepeatPassword();
        if (ObjectUtils.isEmpty(password) || ObjectUtils.isEmpty(repeatPassword)) {
            logger.error("passwords is empty");
            prepareErrorMessage(errors, "user.password.empty", "password");
        } else if (password.length() < minUserPasswordLength) {
            logger.error("password is too short");
            prepareErrorMessage(errors, "user.password.length.short", "password");
        } else if (!password.equals(repeatPassword)) {
            logger.error("passwords mismatch");
            prepareErrorMessage(errors, "user.password.mismatch", "password");
        }
    }

}
