package ru.iteco.project.domain;

import ru.iteco.project.domain.converter.LocalDateTimeAttributeConverter;
import ru.iteco.project.domain.converter.ObjectToJsonAttributeConverter;
import ru.iteco.project.resource.dto.JmsSendStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Модель данных представляющая бухгалтерские отчеты об исполненных контрактах
 */
@Entity
@Table(name = "book_keeping_reports")
public class BookKeepingReport implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id отчета */
    @Id
    @Column
    private UUID id;

    /*** Статус отправки отчета */
    @Column(name = "report_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private JmsSendStatus reportStatus;

    /*** Дата и время создания записи */
    @Column(name = "creation_date", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime creationDate;

    /*** Данные отчетности по заввершенным контрактам */
    @Column(name = "contract_report", columnDefinition = "json", nullable = false)
    @Convert(converter = ObjectToJsonAttributeConverter.class)
    private Map<String, Object> report = new HashMap<>();


    public BookKeepingReport(UUID id, JmsSendStatus reportStatus, LocalDateTime creationDate, Map<String, Object> report) {
        this.id = id;
        this.reportStatus = reportStatus;
        this.creationDate = creationDate;
        this.report = report;
    }

    public BookKeepingReport(UUID id, LocalDateTime creationDate) {
        this.id = id;
        this.creationDate = creationDate;
    }

    public BookKeepingReport() {
    }


    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JmsSendStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(JmsSendStatus reportStatus) {
        this.reportStatus = reportStatus;
    }

    public Map<String, Object> getReport() {
        return report;
    }

    public void setReport(Map<String, Object> report) {
        this.report = report;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
