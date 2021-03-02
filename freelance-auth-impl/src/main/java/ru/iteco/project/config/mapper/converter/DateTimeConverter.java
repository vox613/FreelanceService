package ru.iteco.project.config.mapper.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import ru.iteco.project.service.mappers.DateTimeMapper;

import java.time.LocalDateTime;

/**
 * Класс-конвертер для преобразования типов и форматов дат
 */
@Component
public class DateTimeConverter extends BidirectionalConverter<LocalDateTime, String> {

    @Override
    public String convertTo(LocalDateTime source, Type<String> destinationType, MappingContext mappingContext) {
        return DateTimeMapper.objectToString(source);
    }

    @Override
    public LocalDateTime convertFrom(String source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
        return DateTimeMapper.stringToObject(source);
    }

}
