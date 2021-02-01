package ru.iteco.project.service;

import ru.iteco.project.resource.dto.ConversionDto;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности CurrencyInfo
 */
public interface CurrencyConverterService {

    /***
     * Метод осуществляет преобразование одной валюты в другую
     * @param conversionDto - объект с данными для конвертации
     * @return - объект ConversionDto с установленной конвертированной суммой
     */
    ConversionDto convert(ConversionDto conversionDto);

    /**
     * Метод проверяет существование валюты с переданной текстовой аббревиатурой
     *
     * @param currency - текстовая аббревиатура наименованя валюты
     * @return - true - валюта существует, false -валюта не существует
     */
    Boolean isValidCurrency(String currency);

}
