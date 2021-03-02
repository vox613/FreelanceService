package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.ClientRoleDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса ClientRoleDtoRequest
 */
@Component
public class ClientRoleDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ClientRoleDtoRequestValidator.class.getName());


    public ClientRoleDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ClientRoleDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ClientRoleDtoRequest clientRoleForm = (ClientRoleDtoRequest) target;

        if (ObjectUtils.isEmpty(clientRoleForm.getClientId())) {
            logger.error("Client Id is empty");
            prepareErrorMessage(errors, "roles.client.id.empty", "clientId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientRoleForm.getValue())) {
            logger.error("role value is empty");
            prepareErrorMessage(errors, "roles.value.empty", "value");
        }
    }
}
