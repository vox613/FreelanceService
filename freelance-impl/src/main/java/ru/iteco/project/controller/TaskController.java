package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
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
        List<TaskDtoResponse> allTasks;
        if (userId != null) {
            allTasks = taskService.getAllUserTasks(userId);
        } else {
            allTasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok().body(allTasks);
    }


    @Override
    public ResponseEntity<TaskDtoResponse> getTask(UUID id) {
        TaskDtoResponse taskById = taskService.getTaskById(id);
        if ((taskById != null) && (taskById.getId() != null)) {
            return ResponseEntity.ok().body(taskById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getTasks(TaskSearchDto taskSearchDto, Pageable pageable) {
        return taskService.getTasks(taskSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends TaskBaseDto> createTask(TaskDtoRequest taskDtoRequest,
                                                            BindingResult result,
                                                            UriComponentsBuilder componentsBuilder) {

        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoResponse taskDtoResponse = taskService.createTask(taskDtoRequest);

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
    public ResponseEntity<? extends TaskBaseDto> updateTask(UUID id, TaskDtoRequest taskDtoRequest,
                                                            BindingResult result) {

        if (result.hasErrors()) {
            taskDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }

        TaskDtoResponse taskDtoResponse = taskService.updateTask(taskDtoRequest);

        if (taskDtoResponse != null) {
            return ResponseEntity.ok().body(taskDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(taskDtoRequest);
        }
    }


    @Override
    public ResponseEntity<Object> deleteTask(UUID id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "taskDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(taskDtoRequestValidator);
    }

}
