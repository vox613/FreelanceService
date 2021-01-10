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
import ru.iteco.project.resource.dto.*;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/statuses/tasks")
@Api(value = "API для работы со статусами заданий")
public interface TaskStatusResource {


    /**
     * Контроллер возвращает список всех статусов задания
     *
     * @return - список TaskStatusDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех статусов заданий")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список статусов заданий, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<TaskStatusDtoResponse>> getAllTaskStatus();


    /**
     * Контроллер возвращает TaskStatusDtoResponse статуса задания с заданным id
     *
     * @param id - уникальный идентификатор статуса задания
     * @return TaskStatusDtoResponse заданного статуса
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по статусу задания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о статусе задания, доступная вызывающей стороне",
                    response = TaskStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<TaskStatusDtoResponse> getTaskStatus(@ApiParam(value = "Идентификатор статуса задания", required = true)
                                                        @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param taskStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable            - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по статусам заданий")
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
            @ApiResponse(code = 200, message = "Список найденных статусов заданий, доступных вызывающей стороне",
                    response = TaskStatusDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getTasks(@RequestBody(required = false) TaskStatusSearchDto taskStatusSearchDto,
                     @ApiIgnore
                     @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                             Pageable pageable);

    /**
     * Создает новый статус задания
     *
     * @param taskStatusDtoRequest - тело запроса на создание статуса задания
     * @return Тело запроса на создание статуса задания с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание статуса задания")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Статус задания успешно создан. " +
                    "Ссылка на созданный статус задания в поле заголовка `Location`. " +
                    "Описание самого статуса задания будет возвращено в теле ответа",
                    response = TaskStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends TaskStatusBaseDto> createUserStatus(@Validated @RequestBody TaskStatusDtoRequest taskStatusDtoRequest,
                                                                 BindingResult result,
                                                                 UriComponentsBuilder componentsBuilder);

    /**
     * Обновляет существующий статус задания {id}
     *
     * @param id                   - уникальный идентификатор статуса задания
     * @param taskStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление статуса задания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные статуса задания успешно обновлены",
                    response = TaskStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends TaskStatusBaseDto> updateTaskStatus(@ApiParam(value = "Идентификатор статуса задания", required = true)
                                                                 @PathVariable UUID id,
                                                                 @Validated @RequestBody TaskStatusDtoRequest taskStatusDtoRequest,
                                                                 BindingResult result);

    /**
     * Удаляет статус задания с заданным id
     *
     * @param id - уникальный идентификатор статуса задания для удаления
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление статуса задания")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Статус задания успешно удален",
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
    ResponseEntity<Object> deleteTaskStatus(@ApiParam(value = "Идентификатор статуса задания", required = true)
                                            @PathVariable UUID id);

}
