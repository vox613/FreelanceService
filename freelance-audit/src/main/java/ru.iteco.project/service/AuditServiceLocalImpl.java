package ru.iteco.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.AuditEvent;

/**
 * Класс реализует функционал сервисного слоя для работы с событиями аудита
 * Используется при активированном профиле "local" и осуществляет запись событий аудита в локальный файл или консоль
 */
@Service
@Profile("local")
public class AuditServiceLocalImpl implements AuditService {
    private static final Logger log = LogManager.getLogger(AuditServiceLocalImpl.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


    @Override
    public void createAuditEvent(AuditEvent auditEventDto) {
        try {
            log.debug(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(auditEventDto));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }
}
