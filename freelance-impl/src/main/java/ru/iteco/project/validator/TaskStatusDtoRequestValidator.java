package ru.iteco.project.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;

/**
 * Класс содержит валидаторы для полей объекта запроса TaskStatusDtoRequest
 */
@Component
public class TaskStatusDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(TaskStatusDtoRequestValidator.class.getName());


    public TaskStatusDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return TaskStatusDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        TaskStatusDtoRequest taskStatusDtoRequest = (TaskStatusDtoRequest) target;

        if (ObjectUtils.isEmpty(taskStatusDtoRequest.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "status.task.id.empty", "userId");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskStatusDtoRequest.getValue())) {
            logger.error("status value is empty");
            prepareErrorMessage(errors, "status.task.value.empty", "value");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskStatusDtoRequest.getDescription())) {
            logger.error("status description is empty");
            prepareErrorMessage(errors, "status.task.description.empty", "description");
        }
    }
}
