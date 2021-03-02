package ru.iteco.project.domain.converter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Класс-конвертер для преобразований JSON <-> Object
 */
@Converter
public class ObjectToJsonAttributeConverter<T> implements AttributeConverter<T, String> {
    private static final Logger log = LogManager.getLogger(ObjectToJsonAttributeConverter.class.getName());

    /*** Экземпляр ObjectMapper, предоставляет функциональные возможности для преобразований JSON <-> POJO */
    private final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


    @Override
    public String convertToDatabaseColumn(T attribute) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Ошибка конвертации объекта в JSON!", e);
        }
        return jsonString;
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        T object = null;
        try {
            object = objectMapper.readValue(dbData, new TypeReference<T>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Ошибка конвертации JSON в объект!", e);
        }
        return object;
    }
}
