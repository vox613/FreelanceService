package ru.iteco.project.resource;

import io.swagger.annotations.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.resource.dto.ResponseError;
import ru.iteco.project.resource.dto.TaskBaseDto;
import ru.iteco.project.resource.dto.TaskDtoRequest;
import ru.iteco.project.resource.dto.TaskDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.TaskSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api/v1/tasks")
@Api(value = "API для работы с заданиями")
public interface TaskResource {


    /**
     * Контроллер возвращает список всех заданий, а при наличии RequestParam {userId}
     * возвращаетс список заданий пользователя с соответствующим id
     *
     * @param userId - уникальный идентификатор пользователя
     * @return список объектов TaskDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех созданных заданий или заданий конкретного пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных заданий, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<TaskDtoResponse>> getAllUserTasks(@ApiParam(value = "Идентификатор пользователя")
                                                          @RequestParam(required = false) UUID userId);


    /**
     * Контроллер возвращает TaskDtoResponse задания с заданным id
     *
     * @param id - уникальный идентификатор задания
     * @return TaskDtoResponse заданного задания или пустой TaskDtoResponse, если данное задание не существует
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по заданию")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные задания, доступные вызывающей стороне",
                    response = TaskDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<TaskDtoResponse> getTask(@ApiParam(value = "Идентификатор задания", required = true)
                                            @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param taskSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable      - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по заданиям")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Номер необходимой страницы (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Количество записей на странице"),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Критерии сортировки в формате: критерий(,asc|desc). " +
                            "По умолчанию: (size = 5, page = 0, sort = createdAt,ASC). " +
                            "Поддерживается сортировка по некольким критериям.")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных заданий, доступных вызывающей стороне",
                    response = TaskDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getTasks(@RequestBody(required = false) TaskSearchDto taskSearchDto,
                     @ApiIgnore
                     @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                             Pageable pageable);


    /**
     * Создает новое задание для заказчика
     *
     * @param taskDtoRequest - тело запроса на создание задания
     * @return Тело запроса на создание задания с уникальным проставленным id,
     * * или тело запроса с id = null, если создать задание не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание задания")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Задание успешно создано. " +
                    "Ссылка на созданное задание в поле заголовка `Location`. " +
                    "Описание самого задания будет возвращено в теле ответа",
                    response = TaskDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends TaskBaseDto> createTask(@Validated @RequestBody TaskDtoRequest taskDtoRequest,
                                                     BindingResult result,
                                                     UriComponentsBuilder componentsBuilder);


    /**
     * Обновляет существующее задание {id} от имени заказчика {userId}
     *
     * @param id             - уникальный идентификатор задания
     * @param taskDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление задания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные задания успешно обновлены",
                    response = TaskDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends TaskBaseDto> updateTask(@ApiParam(value = "Идентификатор задания", required = true)
                                                     @PathVariable UUID id,
                                                     @Validated @RequestBody TaskDtoRequest taskDtoRequest,
                                                     BindingResult result);


    /**
     * Удаляет задание с заданным id
     *
     * @param id - уникальный идентификатор задания для удаления
     * @return - объект TaskDtoResponse с данными удаленного задания
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление задания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Задание успешно удалено",
                    response = Object.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<Object> deleteTask(@ApiParam(value = "Идентификатор задания", required = true)
                                      @PathVariable UUID id);
}
