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
import ru.iteco.project.enumaration.AuditCode;
import ru.iteco.project.resource.TaskResource;
import ru.iteco.project.resource.dto.TaskBaseDto;
import ru.iteco.project.resource.dto.TaskDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.TaskSearchDto;
import ru.iteco.project.service.TaskService;
import ru.iteco.project.validator.TaskDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Task
 */
@RestController
public class TaskController implements TaskResource {

    /*** Объект сервисного слоя для Task*/
    private final TaskService taskService;

    /*** Объект валидатора для TaskDtoRequest*/
    private final TaskDtoRequestValidator taskDtoRequestValidator;


    public TaskController(TaskService taskService, TaskDtoRequestValidator taskDtoRequestValidator) {
        this.taskService = taskService;
        this.taskDtoRequestValidator = taskDtoRequestValidator;
    }


    @Override
    public ResponseEntity<List<TaskDtoResponse>> getAllUserTasks(UUID userId) {
        beforeCall(Level.DEBUG, "getAllUserTasks()", userId);
        List<TaskDtoResponse> allTasks;
        if (userId != null) {
            allTasks = taskService.getAllUserTasks(userId);
        } else {
            allTasks = taskService.getAllTasks();
        }
        afterCall(Level.DEBUG, "getAllUserTasks()", allTasks);
        return ResponseEntity.ok().body(allTasks);
    }


    @Override
    public ResponseEntity<TaskDtoResponse> getTask(UUID id) {
        beforeCall(Level.DEBUG, "getTask()", id);
        TaskDtoResponse taskById = taskService.getTaskById(id);
        afterCall(Level.DEBUG, "getTask()", taskById);
        if ((taskById != null) && (taskById.getId() != null)) {
            return ResponseEntity.ok().body(taskById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getTasks(TaskSearchDto taskSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getTasks()", taskSearchDto, pageable);
        PageDto tasks = taskService.getTasks(taskSearchDto, pageable);
        afterCall(Level.DEBUG, "getTasks()", tasks);
        return tasks;
    }


    @Override
    @Audit(operation = AuditCode.TASK_CREATE)
    public ResponseEntity<? extends TaskBaseDto> createTask(TaskDtoRequest taskDtoRequest,
                                                            BindingResult result,
                                                            UriComponentsBuilder componentsBuilder) {

        beforeCall(Level.DEBUG, "createTask()", taskDtoRequest);
        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoResponse taskDtoResponse = taskService.createTask(taskDtoRequest);
        afterCall(Level.DEBUG, "createTask()", taskDtoResponse);

        if (taskDtoResponse != null) {
            URI uri = componentsBuilder
                    .path(String.format("/tasks/%s", taskDtoResponse.getId()))
                    .buildAndExpand(taskDtoResponse)
                    .toUri();

            return ResponseEntity.created(uri).body(taskDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }

    }


    @Override
    @Audit(operation = AuditCode.TASK_UPDATE)
    public ResponseEntity<? extends TaskBaseDto> updateTask(UUID id, TaskDtoRequest taskDtoRequest,
                                                            BindingResult result) {
        beforeCall(Level.DEBUG, "updateTask()", id, taskDtoRequest);
        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoResponse taskDtoResponse = taskService.updateTask(taskDtoRequest);
        afterCall(Level.DEBUG, "updateTask()", taskDtoResponse);
        if (taskDtoResponse != null) {
            return ResponseEntity.ok().body(taskDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }
    }


    @Override
    @Audit(operation = AuditCode.TASK_DELETE)
    public ResponseEntity<Object> deleteTask(UUID id) {
        beforeCall(Level.DEBUG, "deleteTask()", id);
        Boolean isDeleted = taskService.deleteTask(id);
        afterCall(Level.DEBUG, "deleteTask()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "taskDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(taskDtoRequestValidator);
    }

}
