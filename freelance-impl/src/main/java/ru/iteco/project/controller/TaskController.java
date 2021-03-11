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
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.TaskResource;
import ru.iteco.project.resource.dto.TaskBaseDto;
import ru.iteco.project.resource.dto.TaskDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.searching.TaskSearchDto;
import ru.iteco.project.service.TaskService;
import ru.iteco.project.validator.TaskDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.controller.audit.AuditCode.*;
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
    public ResponseEntity<List<TaskDtoResponse>> getAllClientTasks(UUID clientId) {
        beforeCall(Level.DEBUG, "getAllClientTasks()", clientId);
        List<TaskDtoResponse> allTasks;
        if (clientId != null) {
            allTasks = taskService.getAllClientTasks(clientId);
        } else {
            allTasks = taskService.getAllTasks();
        }
        afterCall(Level.DEBUG, "getAllClientTasks()", allTasks);
        return ResponseEntity.ok().body(allTasks);
    }


    @Override
    public ResponseEntity<TaskDtoResponse> getTask(UUID id) {
        beforeCall(Level.DEBUG, "getTask()", id);
        TaskDtoResponse taskById = taskService.getTaskById(id);
        afterCall(Level.DEBUG, "getTask()", taskById);
        return ResponseEntity.ok().body(taskById);
    }


    @Override
    public PageDto getTasks(TaskSearchDto taskSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getTasks()", taskSearchDto, pageable);
        PageDto tasks = taskService.getTasks(taskSearchDto, pageable);
        afterCall(Level.DEBUG, "getTasks()", tasks);
        return tasks;
    }


    @Override
    @Audit(operation = TASK_CREATE)
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

        URI uri = componentsBuilder
                .path(String.format("/api/v1/tasks/%s", taskDtoResponse.getId()))
                .buildAndExpand(taskDtoResponse)
                .toUri();

        return ResponseEntity.created(uri).body(taskDtoResponse);
    }


    @Override
    @Audit(operation = TASK_UPDATE)
    public ResponseEntity<? extends TaskBaseDto> updateTask(UUID id, TaskDtoRequest taskDtoRequest,
                                                            BindingResult result) {
        beforeCall(Level.DEBUG, "updateTask()", id, taskDtoRequest);
        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoResponse taskDtoResponse = taskService.updateTask(taskDtoRequest);
        afterCall(Level.DEBUG, "updateTask()", taskDtoResponse);
        return ResponseEntity.ok().body(taskDtoResponse);
    }


    @Override
    @Audit(operation = TASK_DELETE)
    public ResponseEntity<Object> deleteTask(UUID id) {
        beforeCall(Level.DEBUG, "deleteTask()", id);
        Boolean isDeleted = taskService.deleteTask(id);
        afterCall(Level.DEBUG, "deleteTask()", isDeleted);
        return ResponseEntity.ok().build();
    }


    @InitBinder(value = "taskDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(taskDtoRequestValidator);
    }

}
