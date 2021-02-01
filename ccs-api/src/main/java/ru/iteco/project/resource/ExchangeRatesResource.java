package ru.iteco.project.resource;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.iteco.project.resource.dto.ConversionDto;
import ru.iteco.project.resource.dto.ResponseError;

import java.util.List;

@RequestMapping(path = "/api/v1/exchange")
@Api(value = "API для работы с конвертором валют")
public interface ExchangeRatesResource {

    @PostMapping
    @ApiOperation(value = "Конвертация переданной суммы из одной валюты в другую")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Результат конвертации",
                    response = ConversionDto.class, responseContainer = "ResponseEntity"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class),
            @ApiResponse(code = 401,
                    message = "Полномочия не подтверждены. Например, JWT невалиден, отсутствует, либо неверного формата",
                    response = ResponseError.class),
            @ApiResponse(code = 403, message = "Нет полномочий на выполнение запрашиваемой операции",
                    response = ResponseError.class),
            @ApiResponse(code = 422, message = "Серверу не удалось обработать инструкции содержимого тела запроса",
                    response = ResponseError.class)
    })
    ResponseEntity<ConversionDto> convert(@RequestBody(required = false) ConversionDto conversionDto);


    /**
     * Контроллер проверяет существует ли в базе переданный текстовый код валюты
     *
     * @return - true - валюта существует, false - валюта не существует
     */
    @GetMapping(path = "/{currency}")
    @ApiOperation(value = "Проверка существования в базе переданного текстового кода валюты")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "true - данная валюта существует, false -данная валюта не существует",
                    response = Boolean.class, responseContainer = "Boolean"),
            @ApiResponse(code = 400, message = "Непредвиденная ошибка", response = ResponseError.class)
    })
    Boolean isValidCurrency(@ApiParam(value = "Текстовый код валюты", required = true)
                            @PathVariable String currency);

}