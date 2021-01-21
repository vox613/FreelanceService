package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.audit.AuditEvent;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности AuditEvent
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditEvent, AuditEvent.AuditEventId> {


}
