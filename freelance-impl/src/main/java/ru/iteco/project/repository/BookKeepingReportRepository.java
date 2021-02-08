package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.BookKeepingReport;
import ru.iteco.project.resource.dto.JmsSendStatus;

import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности BookKeepingReport
 */
@Repository
public interface BookKeepingReportRepository extends JpaRepository<BookKeepingReport, UUID> {

    /**
     * Метод удаляет из БД все лог-записи отчетов с переданным статусом
     *
     * @param reportStatus - статус отчетов которые необъходимо удалить
     */
    void deleteAllByReportStatusIs(JmsSendStatus reportStatus);
}
