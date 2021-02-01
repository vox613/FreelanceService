package ru.iteco.project.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.iteco.project.resource.dto.AuditEventDto;
import ru.iteco.project.service.AuditService;

/**
 * Класс реализует функционал сервисного слоя для работы с событиями аудита
 * Используется при активированном профиле "local" и осуществляет запись событий аудита в локальный файл или консоль
 */
@Service
@Profile("local")
public class AuditServiceLocalImpl implements AuditService {
    private static final Logger log = LogManager.getLogger(AuditServiceLocalImpl.class.getName());

    @Override
    public void createAuditEvent(AuditEventDto auditEventDto) {
        try {
            log.debug(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(auditEventDto));
        } catch (JsonProcessingException e) {
            log.error(e);
        }
    }
}
