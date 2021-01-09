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
import ru.iteco.project.controller.dto.UserDtoRequest;

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
        if (ObjectUtils.isEmpty(userForm.getFirstName())) {
            logger.error("firstName is empty");
            prepareErrorMessage(errors, "user.firstName.empty", "firstName");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userForm.getSecondName())) {
            logger.error("secondName is empty");
            prepareErrorMessage(errors, "user.secondName.empty", "secondName");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userForm.getLogin())) {
            logger.error("login is empty");
            prepareErrorMessage(errors, "user.login.empty", "login");
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


        if (ObjectUtils.isEmpty(userForm.getRole())) {
            logger.error("role is empty");
            prepareErrorMessage(errors, "user.role.empty", "role");
        }
        if (errors.hasErrors()) return;


        if (userForm.getWallet() == null) {
            logger.error("wallet is empty");
            prepareErrorMessage(errors, "user.wallet.empty", "wallet");
        } else if (userForm.getWallet().signum() < 0) {
            logger.error("wallet have incorrect negative value");
            prepareErrorMessage(errors, "user.wallet.negative", "wallet");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(userForm.getPassword()) || ObjectUtils.isEmpty(userForm.getRepeatPassword())) {
            logger.error("passwords is empty");
            prepareErrorMessage(errors, "user.password.empty", "repeatPassword");
        } else if (!userForm.getPassword().equals(userForm.getRepeatPassword())) {
            logger.error("passwords mismatch");
            prepareErrorMessage(errors, "user.password.mismatch", "repeatPassword");
        }


        if (ObjectUtils.isEmpty(userForm.getUserStatus())) {
            logger.error("user status empty");
            prepareErrorMessage(errors, "user.status.empty", "userStatus");
        }
    }

}
