package ru.iteco.project.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.AuditEvent;
import ru.iteco.project.repository.AuditRepository;


/**
 * Класс реализует функционал сервисного слоя для работы с событиями аудита
 * Используется при активированном профиле "development" и осуществляет запись событий аудита в БД
 */
@Service
@ConditionalOnProperty(prefix = "audit", value = "destination", havingValue = "DB")
public class AuditServiceDevelopmentImpl implements AuditService {

    /*** Объект доступа к репозиторию событий аудита */
    private final AuditRepository auditRepository;


    public AuditServiceDevelopmentImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void createAuditEvent(AuditEvent auditEvent) {
        auditRepository.save(auditEvent);
    }
}
