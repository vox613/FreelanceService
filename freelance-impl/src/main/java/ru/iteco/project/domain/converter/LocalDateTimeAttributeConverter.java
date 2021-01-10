package ru.iteco.project.domain.converter;

import org.springframework.beans.factory.annotation.Value;
import ru.iteco.project.exception.LocalDateTimeConvertException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Класс-конвертер даты типа LocalDateTime в совместимый с БД sql.Timestamp
 */
@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    /*** Текст ошибки возникающей при попытке сохранения в поле БД null значения */
    @Value("${errors.jpa.converter.data.empty}")
    private String dateToSaveIsEmptyMessage;

    /*** Текст ошибки возникающей при конвертации типов */
    @Value("${errors.jpa.converter.error}")
    private String convertingErrorMessage;


    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new LocalDateTimeConvertException(dateToSaveIsEmptyMessage);
        }
        Timestamp timestamp;
        try {
            timestamp = Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            throw new LocalDateTimeConvertException(convertingErrorMessage, e);
        }
        return timestamp;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp timestamp) {
        if (timestamp == null) {
            throw new LocalDateTimeConvertException(dateToSaveIsEmptyMessage);
        }
        LocalDateTime localDateTime;
        try {
            localDateTime = timestamp.toLocalDateTime();
        } catch (Exception e) {
            throw new LocalDateTimeConvertException(convertingErrorMessage, e);
        }
        return localDateTime;
    }
}