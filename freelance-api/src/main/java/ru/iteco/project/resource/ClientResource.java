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
import ru.iteco.project.resource.dto.ClientBaseDto;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ClientDtoResponse;
import ru.iteco.project.resource.searching.ClientSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api/v1/clients")
@Api(value = "API для работы с пользователями")
public interface ClientResource {

    /**
     * Контроллер возвращает список всех созданных пользователей
     *
     * @return - список ClientDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных пользователей, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<ClientDtoResponse>> getAllClients();


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param clientSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable      - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по пользователям")
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
            @ApiResponse(code = 200, message = "Список найденных пользователей, доступных вызывающей стороне",
                    response = ClientDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getClients(@RequestBody(required = false) ClientSearchDto clientSearchDto,
                       @ApiIgnore
                     @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.DESC)
                             Pageable pageable);


    /**
     * Контроллер возвращает ClientDtoResponse пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя
     * @return ContractDtoResponse заданного пользователя или пустой ContractDtoResponse, если данный пользователь не существует
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по пользователю")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные пользователя, доступные вызывающей стороне",
                    response = ClientDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<ClientDtoResponse> getClient(@ApiParam(value = "Идентификатор пользователя", required = true)
                                            @PathVariable UUID id);


    /**
     * Создает нового пользователя
     *
     * @param clientDtoRequest - тело запроса на создание пользователя
     * @return Тело запроса на создание пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать пользователя не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователь успешно создан. " +
                    "Ссылка на вновь созданного пользователя в поле заголовка `Location`. " +
                    "Описание самого пользователя будет возвращено в теле ответа",
                    response = ClientDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ClientBaseDto> createClient(@Validated @RequestBody ClientDtoRequest clientDtoRequest,
                                                         BindingResult result,
                                                         UriComponentsBuilder componentsBuilder);


    /**
     * Метод пакетного добаввления пользователей
     *
     * @param clientDtoRequestList - список пользователей для добавления
     * @param componentsBuilder  - билдер для формирования url ресура
     * @return - список созданных пользователей в представлении ClientDtoResponse
     */
    @PostMapping(value = "/batch")
    @ApiOperation(value = "Пакетное создание пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователи успешно созданы. " +
                    "Описание пользователей будет возвращено в теле ответа",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<List<? extends Serializable>> createBatchClient(@Validated @RequestBody ArrayList<ClientDtoRequest> clientDtoRequestList,
                                                                   UriComponentsBuilder componentsBuilder,
                                                                   BindingResult result);


    /**
     * Обновляет существующего пользователя {id}
     *
     * @param id             - уникальный идентификатор пользователя
     * @param clientDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные пользователя успешно обновлены",
                    response = ClientDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ClientBaseDto> updateClient(@ApiParam(value = "Идентификатор пользователя", required = true)
                                                     @PathVariable UUID id,
                                                         @Validated @RequestBody ClientDtoRequest clientDtoRequest,
                                                         BindingResult result);


    /**
     * Удаляет пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя для удаления
     * @return - статус 200 если пользователь успешно удален и 404 если такого пользователя нет
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пользователь успешно удален",
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
    ResponseEntity<Object> deleteClient(@ApiParam(value = "Идентификатор пользователя", required = true)
                                      @PathVariable UUID id);

}
