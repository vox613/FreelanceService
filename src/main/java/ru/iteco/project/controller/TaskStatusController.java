package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.TaskStatusBaseDto;
import ru.iteco.project.controller.dto.TaskStatusDtoRequest;
import ru.iteco.project.controller.dto.TaskStatusDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.TaskStatusSearchDto;
import ru.iteco.project.service.TaskStatusService;
import ru.iteco.project.validator.TaskStatusDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с TaskStatus
 */
@RestController
@RequestMapping(path = "/api/v1/statuses/tasks")
public class TaskStatusController {

    /*** Объект сервисного слоя для TaskStatus*/
    private final TaskStatusService taskStatusService;

    /*** Объект валидатора для TaskStatusDtoRequest*/
    private final TaskStatusDtoRequestValidator taskStatusDtoRequestValidator;


    public TaskStatusController(TaskStatusService taskStatusService, TaskStatusDtoRequestValidator taskStatusDtoRequestValidator) {
        this.taskStatusService = taskStatusService;
        this.taskStatusDtoRequestValidator = taskStatusDtoRequestValidator;
    }

    /**
     * Контроллер возвращает список всех статусов задания
     *
     * @return - список TaskStatusDtoResponse
     */
    @GetMapping
    ResponseEntity<List<TaskStatusDtoResponse>> getAllTaskStatus() {
        ArrayList<TaskStatusDtoResponse> allTaskStatuses = taskStatusService.getAllTasksStatuses();
        return ResponseEntity.ok().body(allTaskStatuses);
    }


    /**
     * Контроллер возвращает TaskStatusDtoResponse статуса задания с заданным id
     *
     * @param id - уникальный идентификатор статуса задания
     * @return TaskStatusDtoResponse заданного статуса
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskStatusDtoResponse> getTaskStatus(@PathVariable UUID id) {
        TaskStatusDtoResponse taskStatusById = taskStatusService.getTaskStatusById(id);
        if ((taskStatusById != null) && (taskStatusById.getId() != null)) {
            return ResponseEntity.ok().body(taskStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param taskStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable            - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @GetMapping(path = "/search")
    public PageDto<TaskStatusDtoResponse> getTasks(@RequestBody(required = false) TaskStatusSearchDto taskStatusSearchDto,
                                                   @PageableDefault(size = 5,
                                                           page = 0,
                                                           sort = {"createdAt"},
                                                           direction = Sort.Direction.ASC) Pageable pageable) {

        return taskStatusService.getStatus(taskStatusSearchDto, pageable);
    }


    /**
     * Создает новый статус задания
     *
     * @param taskStatusDtoRequest - тело запроса на создание статуса задания
     * @return Тело запроса на создание статуса задания с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping
    public ResponseEntity<? extends TaskStatusBaseDto> createUserStatus(@Validated @RequestBody TaskStatusDtoRequest taskStatusDtoRequest,
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


    /**
     * Обновляет существующий статус задания {id}
     *
     * @param id                   - уникальный идентификатор статуса задания
     * @param taskStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends TaskStatusBaseDto> updateTaskStatus(@PathVariable UUID id,
                                                                        @Validated @RequestBody TaskStatusDtoRequest taskStatusDtoRequest,
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


    /**
     * Удаляет статус задания с заданным id
     *
     * @param id - уникальный идентификатор статуса задания для удаления
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteTaskStatus(@PathVariable UUID id) {
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
