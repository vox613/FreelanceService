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
import ru.iteco.project.resource.dto.ClientStatusBaseDto;
import ru.iteco.project.resource.dto.ClientStatusDtoRequest;
import ru.iteco.project.resource.dto.ClientStatusDtoResponse;
import ru.iteco.project.resource.dto.ResponseError;
import ru.iteco.project.resource.searching.ClientStatusSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/statuses/clients")
@Api(value = "API для работы со статусами пользователей")
public interface ClientStatusResource {

    /**
     * Контроллер возвращает список всех пользовательских статусов
     *
     * @return - список ClientStatusDtoResponse
     */
    @GetMapping(produces = {"application/json; charset=UTF-8"})
    @ApiOperation(value = "Получение списка всех статусов пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список статусов пользователей, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<ClientStatusDtoResponse>> getAllClientStatus();


    /**
     * Контроллер возвращает ClientStatusDtoResponse статуса с заданным id
     *
     * @param id - уникальный идентификатор статуса пользователя
     * @return ClientStatusDtoResponse заданного статуса или пустой ClientStatusDtoResponse, если данный статус не существует
     */
    @GetMapping(value = "/{id}", produces = {"application/json; charset=UTF-8"})
    @ApiOperation(value = "Детальная информация по статусу пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о статусе пользователя, доступная вызывающей стороне",
                    response = ClientStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<ClientStatusDtoResponse> getClientStatus(@ApiParam(value = "Идентификатор статуса пользователя", required = true)
                                                            @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param clientStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable              - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search", produces = {"application/json; charset=UTF-8"})
    @ApiOperation(value = "Функционал поиска по статусам пользователей")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataTypeClass = Integer.class, paramType = "query",
                    value = "Номер необходимой страницы (0..N)"),
            @ApiImplicitParam(name = "size", dataTypeClass = Integer.class, paramType = "query",
                    value = "Количество записей на странице"),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataTypeClass = String.class, paramType = "query",
                    value = "Критерии сортировки в формате: критерий(,asc|desc). " +
                            "По умолчанию: (size = 5, page = 0, sort = createdAt,ASC). " +
                            "Поддерживается сортировка по некольким критериям.")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных статусов пользователей, доступных вызывающей стороне",
                    response = ClientStatusDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getClients(@RequestBody(required = false) ClientStatusSearchDto clientStatusSearchDto,
                       @ApiIgnore
                       @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                               Pageable pageable);


    /**
     * Создает новый статус пользователя
     *
     * @param clientStatusDtoRequest - тело запроса на создание статуса пользователя
     * @return Тело запроса на создание статуса пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping(produces = {"application/json; charset=UTF-8"})
    @ApiOperation(value = "Создание статуса пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Статус пользователя успешно создан. " +
                    "Ссылка на созданный статус пользователя в поле заголовка `Location`. " +
                    "Описание самого статуса пользователя будет возвращено в теле ответа",
                    response = ClientStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ClientStatusBaseDto> createClientStatus(@Validated @RequestBody ClientStatusDtoRequest clientStatusDtoRequest,
                                                                     BindingResult result,
                                                                     UriComponentsBuilder componentsBuilder);


    /**
     * Обновляет существующий статус пользователя {id}
     *
     * @param id                     - уникальный идентификатор статуса пользователя
     * @param clientStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}", produces = {"application/json; charset=UTF-8"})
    @ApiOperation(value = "Обновление статуса пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные статуса пользователя успешно обновлены",
                    response = ClientStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ClientStatusBaseDto> updateClientStatus(@ApiParam(value = "Идентификатор статуса пользователя", required = true)
                                                                     @PathVariable UUID id,
                                                                     @Validated @RequestBody ClientStatusDtoRequest clientStatusDtoRequest,
                                                                     BindingResult result);


    /**
     * Удаляет статус пользователя с заданным id
     *
     * @param id - уникальный идентификатор статуса для удаления
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление статуса пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Статус пользователя успешно удален",
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
    ResponseEntity<Object> deleteClientStatus(@ApiParam(value = "Идентификатор статуса пользователя", required = true)
                                              @PathVariable UUID id);

}
