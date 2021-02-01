package ru.iteco.project.controller;

import org.apache.logging.log4j.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RestController;
import ru.iteco.project.logger.utils.LoggerUtils;
import ru.iteco.project.resource.ExchangeRatesResource;
import ru.iteco.project.resource.dto.ConversionDto;
import ru.iteco.project.service.CurrencyConverterService;
import ru.iteco.project.validator.ConversionDtoValidator;


@RestController
public class ExchangeRatesController implements ExchangeRatesResource {

    /*** Объект доступа к сервисам валют */
    private final CurrencyConverterService currencyConverterService;

    /*** Объект валидатора ConversionDto */
    private final ConversionDtoValidator conversionDtoValidator;


    public ExchangeRatesController(CurrencyConverterService currencyConverterService, ConversionDtoValidator conversionDtoValidator) {
        this.currencyConverterService = currencyConverterService;
        this.conversionDtoValidator = conversionDtoValidator;
    }

    @Override
    public ResponseEntity<ConversionDto> convert(ConversionDto conversionDto) {
        LoggerUtils.beforeCall(Level.INFO, "convert()", conversionDto);
        if (conversionDto != null) {
            Errors errors = new BeanPropertyBindingResult(conversionDto, conversionDto.getClass().getName());
            conversionDtoValidator.validate(conversionDto, errors);
            if (errors.hasErrors()) {
                conversionDto.setErrors(errors.getAllErrors());
                return ResponseEntity.unprocessableEntity().body(conversionDto);
            }
            ConversionDto convert = currencyConverterService.convert(conversionDto);
            ResponseEntity<ConversionDto> responseEntity = ResponseEntity.ok().body(convert);
            LoggerUtils.afterCall(Level.INFO, "convert()", responseEntity);
            return responseEntity;
        }
        ResponseEntity responseEntity = ResponseEntity.unprocessableEntity().build();
        LoggerUtils.afterCall(Level.INFO, "convert()", responseEntity);
        return responseEntity;
    }

    @Override
    public Boolean isValidCurrency(String currency) {
        LoggerUtils.beforeCall(Level.INFO, "isValidCurrency()", currency);
        Boolean isValidCurrency = currencyConverterService.isValidCurrency(currency);
        LoggerUtils.afterCall(Level.INFO, "isValidCurrency()", isValidCurrency);
        return isValidCurrency;
    }
}
