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
import ru.iteco.project.resource.searching.UserRoleSearchDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/roles/users")
@Api(value = "API для работы с ролями пользователей")
public interface UserRoleResource {

    /**
     * Контроллер возвращает список всех пользовательских ролей
     *
     * @return - список UserDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех ролей пользователей")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список ролей пользователей, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<UserRoleDtoResponse>> getAllUserRole();


    /**
     * Контроллер возвращает UserRoleDtoResponse роли с заданным id
     *
     * @param id - уникальный идентификатор роли пользователя
     * @return UserRoleDtoResponse заданной роли или пустой UserRoleDtoResponse, если данная роль не существует
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по роли пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о роли пользователя, доступная вызывающей стороне",
                    response = UserRoleDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<UserRoleDtoResponse> getUserRole(@ApiParam(value = "Идентификатор роли пользователя", required = true)
                                                    @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param userRoleSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable          - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по ролям пользователей")
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
            @ApiResponse(code = 200, message = "Список найденных ролей пользователей, доступных вызывающей стороне",
                    response = UserRoleDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getUsers(@RequestBody(required = false) UserRoleSearchDto userRoleSearchDto,
                     @ApiIgnore
                     @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                             Pageable pageable);


    /**
     * Создает новую роль пользователя
     *
     * @param userRoleDtoRequest - тело запроса на создание роли пользователя
     * @return Тело запроса на создание роли пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать роли не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание роли пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Роль пользователя успешно создана. " +
                    "Ссылка на созданную роль пользователя в поле заголовка `Location`. " +
                    "Описание самой роли пользователя будет возвращено в теле ответа",
                    response = UserRoleDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends UserRoleBaseDto> createUserRole(@Validated @RequestBody UserRoleDtoRequest userRoleDtoRequest,
                                                             BindingResult result,
                                                             UriComponentsBuilder componentsBuilder);


    /**
     * Обновляет существующую роли пользователя {id}
     *
     * @param id                 - уникальный идентификатор роли пользователя
     * @param userRoleDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление роли пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные роли пользователя успешно обновлены",
                    response = UserRoleDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends UserRoleBaseDto> updateUserRole(@ApiParam(value = "Идентификатор роли пользователя", required = true)
                                                             @PathVariable UUID id,
                                                             @Validated @RequestBody UserRoleDtoRequest userRoleDtoRequest,
                                                             BindingResult result);


    /**
     * Удаляет роль пользователя с заданным id
     *
     * @param id - уникальный идентификатор роли для удаления
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление роли пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Роль пользователя успешно удалена",
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
    ResponseEntity<Object> deleteUser(@ApiParam(value = "Идентификатор роли пользователя", required = true)
                                      @PathVariable UUID id);

}
