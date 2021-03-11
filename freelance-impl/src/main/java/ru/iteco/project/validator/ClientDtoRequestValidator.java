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
import ru.iteco.project.resource.dto.ClientDtoRequest;

import java.util.ArrayList;

/**
 * Класс содержит валидаторы для полей объекта запроса ClientDtoRequest
 */
@Component
@PropertySource(value = {"classpath:application.yml"})
public class ClientDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ClientDtoRequestValidator.class.getName());

    @Value("${client.email.regexp}")
    private String emailRegExpValidator;

    @Value("${client.phone.regexp}")
    private String phoneRegExpValidator;

    public ClientDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ClientDtoRequest.class.equals(clazz) || ArrayList.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target instanceof ClientDtoRequest) {
            ClientDtoRequest clientForm = (ClientDtoRequest) target;
            performClientDtoRequestChecks(clientForm, errors);
        }
        if (target instanceof ArrayList) {
            ArrayList<ClientDtoRequest> clientFormsList = (ArrayList<ClientDtoRequest>) target;
            performClientDtoRequestListChecks(clientFormsList, errors);
        }
    }

    private void performClientDtoRequestListChecks(ArrayList<ClientDtoRequest> clientFormsList, Errors errors) {
        clientFormsList.forEach(clientDtoRequest -> performClientDtoRequestChecks(clientDtoRequest, errors));
    }

    private void performClientDtoRequestChecks(ClientDtoRequest clientForm, Errors errors) {
        if (ObjectUtils.isEmpty(clientForm.getFirstName())) {
            logger.error("firstName is empty");
            prepareErrorMessage(errors, "client.firstName.empty", "firstName");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientForm.getSecondName())) {
            logger.error("secondName is empty");
            prepareErrorMessage(errors, "client.secondName.empty", "secondName");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientForm.getEmail())) {
            logger.error("email is empty");
            prepareErrorMessage(errors, "client.email.empty", "email");
        } else if (!clientForm.getEmail().matches(emailRegExpValidator)) {
            logger.error("email is incorrect");
            prepareErrorMessage(errors, "client.email.incorrect", "email");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientForm.getPhoneNumber())) {
            logger.error("phone is empty");
            prepareErrorMessage(errors, "client.phone.empty", "phoneNumber");
        } else if (!clientForm.getPhoneNumber().matches(phoneRegExpValidator)) {
            logger.error("phone is incorrect");
            prepareErrorMessage(errors, "client.phone.incorrect", "phoneNumber");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientForm.getClientRole())) {
            logger.error("role is empty");
            prepareErrorMessage(errors, "client.role.empty", "role");
        }
        if (errors.hasErrors()) return;


        if (clientForm.getWallet() == null) {
            logger.error("wallet is empty");
            prepareErrorMessage(errors, "client.wallet.empty", "wallet");
        } else if (clientForm.getWallet().signum() < 0) {
            logger.error("wallet have incorrect negative value");
            prepareErrorMessage(errors, "client.wallet.negative", "wallet");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientForm.getClientStatus())) {
            logger.error("Client status empty");
            prepareErrorMessage(errors, "client.status.empty", "clientStatus");
        }
    }

}
