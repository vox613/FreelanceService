package ru.iteco.project.service;

import ru.iteco.project.domain.AuditEvent;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности AuditEvent
 */
public interface AuditService {

    /**
     * Метод создает запись события в аудите
     *
     * @param auditEvent - Сущность с данными для создания записи аудита
     */
    void createAuditEvent(AuditEvent auditEvent);

}

