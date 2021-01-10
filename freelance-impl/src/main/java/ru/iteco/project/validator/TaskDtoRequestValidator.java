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
import ru.iteco.project.resource.dto.TaskDtoRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс содержит валидаторы для полей объекта запроса TaskDtoRequest
 */
@Component
@PropertySource(value = {"classpath:application.yml"})
public class TaskDtoRequestValidator extends AbstractDtoValidator implements Validator {
    private static final Logger logger = LogManager.getLogger(TaskDtoRequestValidator.class.getName());

    /*** Установленный формат даты и времени */
    @Value("${format.date.time}")
    private String formatDateTime;

    /*** Максимальная длина описания задания */
    @Value("${task.description.length.max}")
    private Integer taskDescriptionMaxLength;

    /*** Минимальная стоимость задания задания */
    @Value("${task.price.min}")
    private String taskMinPrice;

    /*** Максимальная длина решени задания */
    @Value("${task.decision.length.max}")
    private Integer taskDecisionMaxLength;


    public TaskDtoRequestValidator(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return TaskDtoRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        TaskDtoRequest taskForm = (TaskDtoRequest) target;

        if (ObjectUtils.isEmpty(taskForm.getUserId())) {
            logger.error("user Id is empty");
            prepareErrorMessage(errors, "task.user.id.empty", "userId");
        }
        if (errors.hasErrors()) return;

        if (ObjectUtils.isEmpty(taskForm.getCustomerId())) {
            logger.error("customerId is empty");
            prepareErrorMessage(errors, "task.customer.id.empty", "name");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskForm.getTitle())) {
            logger.error("taskTitle is empty");
            prepareErrorMessage(errors, "task.title.empty", "title");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskForm.getDescription())) {
            logger.error("description is empty");
            prepareErrorMessage(errors, "task.description.empty", "description");
        } else if (taskDescriptionMaxLength < taskForm.getDescription().length()) {
            logger.error("the description is too long");
            prepareErrorMessage(errors, "task.description.length", "description");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskForm.getTaskCompletionDate())) {
            logger.error("taskCompletionDate is empty");
            prepareErrorMessage(errors, "task.date.completion.empty", "taskCompletionDate");
        } else if (!dateTimeFormatIsValid(taskForm)) {
            prepareErrorMessage(errors, "task.date.completion.format", "taskCompletionDate");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskForm.getPrice())) {
            logger.error("task price is empty");
            prepareErrorMessage(errors, "task.price.empty", "price");
        } else if (!priceIsValid(taskForm)) {
            logger.error("task price less than the minimum allowable");
            prepareErrorMessage(errors, "task.price.min", "price");
        }
        if (errors.hasErrors()) return;


        String taskDecision = taskForm.getTaskDecision();
        if (!ObjectUtils.isEmpty(taskDecision) && taskDecision.length() > taskDecisionMaxLength) {
            logger.error("The length of the task solution exceeds the maximum number of characters");
            prepareErrorMessage(errors, "task.decision.length.max", "taskDecision");
        }
        if (errors.hasErrors()) return;


        if (ObjectUtils.isEmpty(taskForm.getTaskStatus())) {
            logger.error("task status is empty");
            prepareErrorMessage(errors, "task.status.empty", "taskStatus");
        }

    }

    /**
     * Метод проверяет введенную дату и время на соответствие общему формату
     *
     * @param taskForm - объект запроса
     * @return true - дата и время валидны, false - не валидны
     */
    private boolean dateTimeFormatIsValid(TaskDtoRequest taskForm) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatDateTime);
        try {
            LocalDateTime.parse(taskForm.getTaskCompletionDate(), dateTimeFormatter);
            return true;
        } catch (Exception e) {
            logger.error(String.format("An error occurred while parsing the date! Input value: %s , Expected format: %s",
                    taskForm.getTaskCompletionDate(), formatDateTime));
            return false;
        }
    }

    /**
     * Метод проверяет удовлетворяет ли введенная сумма установленным ограничениям
     *
     * @param taskForm - объект запроса
     * @return true - сумма валидна, false - не валидна
     */
    private boolean priceIsValid(TaskDtoRequest taskForm) {
        BigDecimal minPrice = new BigDecimal(taskMinPrice);
        return taskForm.getPrice().compareTo(minPrice) >= 0;
    }

}
