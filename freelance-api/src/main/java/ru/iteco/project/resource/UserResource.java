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
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.UserSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api/v1/users")
@Api(value = "API для работы с пользователями")
public interface UserResource {

    /**
     * Контроллер возвращает список всех созданных пользователей
     *
     * @return - список UserDtoResponse
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
    ResponseEntity<List<UserDtoResponse>> getAllUsers();


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param userSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
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
                    response = UserDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getUsers(@RequestBody(required = false) UserSearchDto userSearchDto,
                     @ApiIgnore
                     @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.DESC)
                             Pageable pageable);


    /**
     * Контроллер возвращает UserDtoResponse пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя
     * @return ContractDtoResponse заданного пользователя или пустой ContractDtoResponse, если данный пользователь не существует
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по пользователю")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные пользователя, доступные вызывающей стороне",
                    response = UserDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<UserDtoResponse> getUser(@ApiParam(value = "Идентификатор пользователя", required = true)
                                            @PathVariable UUID id);


    /**
     * Создает нового пользователя
     *
     * @param userDtoRequest - тело запроса на создание пользователя
     * @return Тело запроса на создание пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать пользователя не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Пользователь успешно создан. " +
                    "Ссылка на вновь созданного пользователя в поле заголовка `Location`. " +
                    "Описание самого пользователя будет возвращено в теле ответа",
                    response = UserDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends UserBaseDto> createUser(@Validated @RequestBody UserDtoRequest userDtoRequest,
                                                     BindingResult result,
                                                     UriComponentsBuilder componentsBuilder);


    /**
     * Метод пакетного добаввления пользователей
     *
     * @param userDtoRequestList - список пользователей для добавления
     * @param componentsBuilder  - билдер для формирования url ресура
     * @return - список созданных пользователей в представлении UserDtoResponse
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
    ResponseEntity<List<? extends Serializable>> createBatchUser(@Validated @RequestBody ArrayList<UserDtoRequest> userDtoRequestList,
                                                                 UriComponentsBuilder componentsBuilder,
                                                                 BindingResult result);


    /**
     * Обновляет существующего пользователя {id}
     *
     * @param id             - уникальный идентификатор пользователя
     * @param userDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные пользователя успешно обновлены",
                    response = UserDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends UserBaseDto> updateUser(@ApiParam(value = "Идентификатор пользователя", required = true)
                                                     @PathVariable UUID id,
                                                     @Validated @RequestBody UserDtoRequest userDtoRequest,
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
    ResponseEntity<Object> deleteUser(@ApiParam(value = "Идентификатор пользователя", required = true)
                                      @PathVariable UUID id);

}
