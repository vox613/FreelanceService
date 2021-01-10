package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.ContractStatusDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса ContractStatusDtoRequest
 */
@Component
public class ContractStatusDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ContractStatusDtoRequestValidator.class.getName());


    public ContractStatusDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ContractStatusDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ContractStatusDtoRequest contractStatusDtoRequest = (ContractStatusDtoRequest) target;

        if (ObjectUtils.isEmpty(contractStatusDtoRequest.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "status.contract.id.empty", "userId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(contractStatusDtoRequest.getValue())) {
            logger.error("status value is empty");
            prepareErrorMessage(errors, "status.contract.value.empty", "value");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(contractStatusDtoRequest.getDescription())) {
            logger.error("status description is empty");
            prepareErrorMessage(errors, "status.contract.description.empty", "description");
        }
    }
}
