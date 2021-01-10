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
import ru.iteco.project.resource.searching.ContractStatusSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@RequestMapping(path = "/api/v1/statuses/contracts")
@Api(value = "API для работы со статусами контрактов")
public interface ContractStatusResource {


    /**
     * Контроллер возвращает список всех статусов контрактов
     *
     * @return - список ContractStatusDtoResponse
     */
    @GetMapping
    @ApiOperation(value = "Получение списка всех статусов контрактов")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Список статусов контрактов, доступных вызывающей стороне",
                    response = List.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    ResponseEntity<List<ContractStatusDtoResponse>> getAllContractStatus();


    /**
     * Контроллер возвращает ContractStatusDtoResponse статуса контракта с заданным id
     *
     * @param id - уникальный идентификатор статуса контракта
     * @return ContractStatusDtoResponse заданного статуса
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "Детальная информация по статусу контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о статусе контракта, доступная вызывающей стороне",
                    response = ContractStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 404, message = "Запись с заданным id не найдена",
                    response = ResponseError.class)
    })
    ResponseEntity<ContractStatusDtoResponse> getContractStatus(@ApiParam(value = "Идентификатор статуса контракта", required = true)
                                                                @PathVariable UUID id);


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param contractStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable                - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @PostMapping(path = "/search")
    @ApiOperation(value = "Функционал поиска по статусам контрактов")
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
            @ApiResponse(code = 200, message = "Список найденных статусов контрактов, доступных вызывающей стороне",
                    response = ContractStatusDtoResponse.class, responseContainer = "PageDto"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class)
    })
    PageDto getContracts(@RequestBody(required = false) ContractStatusSearchDto contractStatusSearchDto,
                         @ApiIgnore
                         @PageableDefault(size = 5, page = 0, sort = {"createdAt"}, direction = Sort.Direction.ASC)
                                 Pageable pageable);

    /**
     * Создает новый статус контракта
     *
     * @param contractStatusDtoRequest - тело запроса на создание статуса контракта
     * @return Тело запроса на создание статуса контракта с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping
    @ApiOperation(value = "Создание статуса контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Статус контракта успешно создан. " +
                    "Ссылка на созданный статус контракта в поле заголовка `Location`. " +
                    "Описание самого статуса контракта будет возвращено в теле ответа",
                    response = ContractStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ContractStatusBaseDto> createContractStatus(@Validated @RequestBody ContractStatusDtoRequest contractStatusDtoRequest,
                                                                         BindingResult result,
                                                                         UriComponentsBuilder componentsBuilder);

    /**
     * Обновляет существующий статус контракта {id}
     *
     * @param id                       - уникальный идентификатор статуса контракта
     * @param contractStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    @ApiOperation(value = "Обновление статуса контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Данные статуса контракта успешно обновлены",
                    response = ContractStatusDtoResponse.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<? extends ContractStatusBaseDto> updateTaskStatus(@ApiParam(value = "Идентификатор статуса контракта", required = true)
                                                                     @PathVariable UUID id,
                                                                     @Validated @RequestBody ContractStatusDtoRequest contractStatusDtoRequest,
                                                                     BindingResult result);

    /**
     * Удаляет статус контракта с заданным id
     *
     * @param id - уникальный идентификатор статуса контракта для удаления
     */
    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Удаление статуса контракта")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Статус контракта успешно удален",
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
    ResponseEntity<Object> deleteTaskStatus(@ApiParam(value = "Идентификатор статуса контракта", required = true)
                                            @PathVariable UUID id);

}
