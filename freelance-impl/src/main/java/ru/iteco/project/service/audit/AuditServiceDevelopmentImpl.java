package ru.iteco.project.service.audit;

import ma.glasnost.orika.MapperFacade;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.audit.AuditEvent;
import ru.iteco.project.dto.AuditEventDto;
import ru.iteco.project.repository.AuditRepository;
import ru.iteco.project.service.AuditService;


/**
 * Класс реализует функционал сервисного слоя для работы с событиями аудита
 * Используется при активированном профиле "development" и осуществляет запись событий аудита в БД
 */
@Service
@Profile("development")
public class AuditServiceDevelopmentImpl implements AuditService {

    /*** Объект доступа к репозиторию событий аудита */
    private final AuditRepository auditRepository;

    /*** Объект маппера dto <-> сущность задания */
    private final MapperFacade mapperFacade;

    public AuditServiceDevelopmentImpl(AuditRepository auditRepository, MapperFacade mapperFacade) {
        this.auditRepository = auditRepository;
        this.mapperFacade = mapperFacade;
    }

    @Override
    public void createAuditEvent(AuditEventDto auditEventDto) {
        auditRepository.save(mapperFacade.map(auditEventDto, AuditEvent.class));
    }
}
