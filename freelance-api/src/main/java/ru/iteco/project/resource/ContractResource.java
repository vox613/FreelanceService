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
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(value = "/api/v1/contracts")
@Api(value = "API для работы с контрактами")
public interface ContractResource {

    /**
     * Контроллер возвращает список всех созданных контрактов
     *
     * @return - список ContractDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех контрактов")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных контрактов, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<ContractDtoResponse>> getAllContracts();


    /**
     * Контроллер возвращает ContractDtoResponse контракта с заданным id
     *
     * @param id - уникальный идентификатор контракта
     * @return ContractDtoResponse заданного контракта или пустой ContractDtoResponse, если данный контракт не существует
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по контракту")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные контракта, доступные вызывающей стороне",
                    response = ContractDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<ContractDtoResponse> getContract(@ApiParam(value = "Идентификатор контракта", required = true)
                                                    @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param contractSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable          - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по контрактам")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Номер необходимой страницы (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Количество записей на странице"),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Критерии сортировки в формате: критерий (,asc|desc). " +
                            "По умолчанию: (size = 5, page = 0, sort = createdAt,ASC). " +
                            "Поддерживается сортировка по некольким критериям.")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список найденных контрактов, доступных вызывающей стороне",
                    response = ContractDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getContracts(@RequestBody(required = false) ContractSearchDto contractSearchDto,
                         @ApiIgnore
                         @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                                 Pageable pageable);

    /**
     * Создает новый контракт для исполнителя {userId}
     *
     * @param contractDtoRequest - тело запроса на создание контракта
     * @return Тело запроса на создание контракта с уникальным проставленным id,
     * или тело запроса с id = null, если создать контракт не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Контракт успешно создан. " +
                    "Ссылка на созданный контракт в поле заголовка `Location`. " +
                    "Описание самого контракта будет возвращено в теле ответа",
                    response = ContractDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ContractBaseDto> createContract(@Validated @RequestBody ContractDtoRequest contractDtoRequest,
                                                             UriComponentsBuilder componentsBuilder,
                                                             BindingResult result);

    /**
     * Обновляет существующий контракт {id} от имени пользователя {userId}
     *
     * @param id                 - уникальный идентификатор контракта
     * @param contractDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные контракта успешно обновлены",
                    response = ContractDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ContractBaseDto> updateContract(@Validated @RequestBody ContractDtoRequest contractDtoRequest,
                                                             @ApiParam(value = "Идентификатор контракта", required = true)
                                                             @PathVariable UUID id,
                                                             BindingResult result);

    /**
     * Удаляет контракт с заданным id
     *
     * @param id - уникальный идентификатор контракта для удаления
     * @return - объект ContractDtoResponse с данными удаленного контракта
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Контракт успешно удален",
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
    ResponseEntity<ContractDtoResponse> deleteContract(@ApiParam(value = "Идентификатор контракта", required = true)
                                                       @PathVariable UUID id);

}
