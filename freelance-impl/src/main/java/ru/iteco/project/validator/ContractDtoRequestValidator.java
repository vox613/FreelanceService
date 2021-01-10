package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.exception.ContractConclusionException;

/**
 * Класс содержит валидаторы для полей объекта запроса ContractDtoRequest
 */
@Component
public class ContractDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(ContractDtoRequestValidator.class.getName());


    public ContractDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ContractDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ContractDtoRequest contractForm = (ContractDtoRequest) target;

        if (ObjectUtils.isEmpty(contractForm.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "contract.user.id.empty", "userId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(contractForm.getExecutorId())) {
            logger.error("contract executorId is empty");
            prepareErrorMessage(errors, "contract.executor.id.empty", "executorId");
        }


        if (ObjectUtils.isEmpty(contractForm.getTaskId())) {
            logger.error("contract taskId is empty");
            prepareErrorMessage(errors, "contract.task.id.empty", "taskId");
        }


        String confirmationCode = contractForm.getConfirmationCode();
        String repeatConfirmationCode = contractForm.getRepeatConfirmationCode();
        if (ObjectUtils.isEmpty(confirmationCode) || ObjectUtils.isEmpty(repeatConfirmationCode)) {
            logger.error("contract confirmation code is empty");
            prepareErrorMessage(errors, "contract.confirmation.code.empty", "repeatConfirmationCode");
        } else if (!confirmationCode.equals(repeatConfirmationCode)) {
            logger.error("confirmation codes mismatch");
            prepareErrorMessage(errors, "contract.confirmation.code.mismatch", "repeatConfirmationCode");
        }


        if (ObjectUtils.isEmpty(contractForm.getContractStatus())) {
            logger.error("contract status is empty");
            prepareErrorMessage(errors, "contract.status.empty", "contractStatus");
        }

        if (errors.hasErrors()) {
            throw new ContractConclusionException("Impossible to prepare a contract, insufficient data!", errors.getAllErrors());
        }

    }

}
