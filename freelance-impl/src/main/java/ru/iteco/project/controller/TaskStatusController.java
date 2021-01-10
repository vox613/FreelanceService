package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.resource.TaskStatusResource;
import ru.iteco.project.resource.dto.TaskStatusBaseDto;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;
import ru.iteco.project.resource.dto.TaskStatusDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;
import ru.iteco.project.service.TaskStatusService;
import ru.iteco.project.validator.TaskStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
        List<TaskStatusDtoResponse> allTaskStatuses = taskStatusService.getAllTasksStatuses();
        return ResponseEntity.ok().body(allTaskStatuses);
    }


    @Override
    public ResponseEntity<TaskStatusDtoResponse> getTaskStatus(UUID id) {
        TaskStatusDtoResponse taskStatusById = taskStatusService.getTaskStatusById(id);
        if ((taskStatusById != null) && (taskStatusById.getId() != null)) {
            return ResponseEntity.ok().body(taskStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getTasks(@RequestBody(required = false) TaskStatusSearchDto taskStatusSearchDto,
                            @PageableDefault(size = 5,
                                    page = 0,
                                    sort = {"createdAt"},
                                    direction = Sort.Direction.ASC) Pageable pageable) {

        return taskStatusService.getStatus(taskStatusSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends TaskStatusBaseDto> createUserStatus(TaskStatusDtoRequest taskStatusDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            taskStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskStatusDtoRequest);
        }

        TaskStatusDtoResponse taskStatusDtoResponse = taskStatusService.createTaskStatus(taskStatusDtoRequest);

        if (taskStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("statuses/tasks/" + taskStatusDtoResponse.getId()).buildAndExpand(taskStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(taskStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    public ResponseEntity<? extends TaskStatusBaseDto> updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest,
                                                                        BindingResult result) {

        if (result.hasErrors()) {
            taskStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(taskStatusDtoRequest);
        }

        TaskStatusDtoResponse taskStatusDtoResponse = taskStatusService.updateTaskStatus(id, taskStatusDtoRequest);

        if (taskStatusDtoResponse != null) {
            return ResponseEntity.ok().body(taskStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    @Override
    public ResponseEntity<Object> deleteTaskStatus(UUID id) {
        if (taskStatusService.deleteTaskStatus(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "taskStatusDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(taskStatusDtoRequestValidator);
    }

}
