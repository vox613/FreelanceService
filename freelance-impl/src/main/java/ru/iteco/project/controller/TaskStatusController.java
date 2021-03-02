package ru.iteco.project.controller;

import org.apache.logging.log4j.Level;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.resource.TaskStatusResource;
import ru.iteco.project.resource.dto.TaskStatusBaseDto;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;
import ru.iteco.project.resource.dto.TaskStatusDtoResponse;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;
import ru.iteco.project.service.TaskStatusService;
import ru.iteco.project.validator.TaskStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;
import static ru.iteco.project.controller.audit.AuditCode.*;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с TaskStatus
 */
@RestController
public class TaskStatusController implements TaskStatusResource {

    /*** Объект сервисного слоя для TaskStatus*/
    private final TaskStatusService taskStatusService;

    /*** Объект валидатора для TaskStatusDtoRequest*/
    private final TaskStatusDtoRequestValidator taskStatusDtoRequestValidator;


    public TaskStatusController(TaskStatusService taskStatusService, TaskStatusDtoRequestValidator taskStatusDtoRequestValidator) {
        this.taskStatusService = taskStatusService;
        this.taskStatusDtoRequestValidator = taskStatusDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<TaskStatusDtoResponse>> getAllTaskStatus() {
        beforeCall(Level.DEBUG, "getAllTaskStatus()", "{}");
        List<TaskStatusDtoResponse> allTaskStatuses = taskStatusService.getAllTasksStatuses();
        afterCall(Level.DEBUG, "getAllTaskStatus()", allTaskStatuses);
        return ResponseEntity.ok().body(allTaskStatuses);
    }


    @Override
    public ResponseEntity<TaskStatusDtoResponse> getTaskStatus(UUID id) {
        beforeCall(Level.DEBUG, "getTaskStatus()", id);
        TaskStatusDtoResponse taskStatusById = taskStatusService.getTaskStatusById(id);
        afterCall(Level.DEBUG, "getTaskStatus()", taskStatusById);
        if ((taskStatusById != null) && (taskStatusById.getId() != null)) {
            return ResponseEntity.ok().body(taskStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getTasks(TaskStatusSearchDto taskStatusSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getTasks()", taskStatusSearchDto, pageable);
        PageDto taskStatus = taskStatusService.getStatus(taskStatusSearchDto, pageable);
        afterCall(Level.DEBUG, "getTasks()", taskStatus);
        return taskStatus;
    }


    @Override
    @Audit(operation = TASK_STATUS_CREATE)
    public ResponseEntity<? extends TaskStatusBaseDto> createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createTaskStatus()", taskStatusDtoRequest);
        if (result.hasErrors()) {
            taskStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskStatusDtoRequest);
        }

        TaskStatusDtoResponse taskStatusDtoResponse = taskStatusService.createTaskStatus(taskStatusDtoRequest);
        afterCall(Level.DEBUG, "createTaskStatus()", taskStatusDtoResponse);

        if (taskStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("statuses/tasks/" + taskStatusDtoResponse.getId()).buildAndExpand(taskStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(taskStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    @Audit(operation = TASK_STATUS_UPDATE)
    public ResponseEntity<? extends TaskStatusBaseDto> updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest,
                                                                        BindingResult result) {
        beforeCall(Level.DEBUG, "updateTaskStatus()", id, taskStatusDtoRequest);
        if (result.hasErrors()) {
            taskStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskStatusDtoRequest);
        }

        TaskStatusDtoResponse taskStatusDtoResponse = taskStatusService.updateTaskStatus(id, taskStatusDtoRequest);
        afterCall(Level.DEBUG, "updateTaskStatus()", taskStatusDtoResponse);

        if (taskStatusDtoResponse != null) {
            return ResponseEntity.ok().body(taskStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    @Override
    @Audit(operation = TASK_STATUS_DELETE)
    public ResponseEntity<Object> deleteTaskStatus(UUID id) {
        beforeCall(Level.DEBUG, "deleteTaskStatus()", id);
        Boolean isDeleted = taskStatusService.deleteTaskStatus(id);
        afterCall(Level.DEBUG, "deleteTaskStatus()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "taskStatusDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(taskStatusDtoRequestValidator);
    }

}
