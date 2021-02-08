package ru.iteco.project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.ContractReport;
import ru.iteco.project.resource.dto.JmsSendStatus;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности ContractReport
 */
@Repository
public interface ContractReportsRepository extends JpaRepository<ContractReport, UUID> {

    Collection<ContractReport> findAllByAckStatusIsNot(JmsSendStatus ackStatus);

}
