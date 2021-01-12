package ru.iteco.project.service;

import ru.iteco.project.dto.AuditEventDto;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности AuditEvent
 */
public interface AuditService {

    /**
     * Метод создает запись события в аудите
     *
     * @param auditEventDto - модель с данными для создания записи аудита
     */
    void createAuditEvent(AuditEventDto auditEventDto);

}

