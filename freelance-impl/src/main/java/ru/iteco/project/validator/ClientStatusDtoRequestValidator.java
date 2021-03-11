package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.ClientStatusDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса ClientStatusDtoRequest
 */
@Component
public class ClientStatusDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ClientStatusDtoRequestValidator.class.getName());


    public ClientStatusDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ClientStatusDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ClientStatusDtoRequest clientStatusDtoRequest = (ClientStatusDtoRequest) target;

        if (ObjectUtils.isEmpty(clientStatusDtoRequest.getValue())) {
            logger.error("status value is empty");
            prepareErrorMessage(errors, "status.client.value.empty", "value");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(clientStatusDtoRequest.getDescription())) {
            logger.error("status description is empty");
            prepareErrorMessage(errors, "status.client.description.empty", "description");
        }
    }
}
