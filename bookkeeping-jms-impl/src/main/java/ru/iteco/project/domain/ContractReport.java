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
@Table(name = "book_keeping_reports", schema = "bookkeeping")
public class ContractReport implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id отчета */
    @Id
    @Column
    private UUID id;

    /*** Статус отправки отчета */
    @Column(name = "ack_status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private JmsSendStatus ackStatus;

    /*** Дата и время создания записи */
    @Column(name = "creation_date", nullable = false)
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime creationDate;

    /*** Данные отчетности по заввершенным контрактам */
    @Column(name = "contract_report", columnDefinition = "json", nullable = false)
    @Convert(converter = ObjectToJsonAttributeConverter.class)
    private Map<String, Object> report = new HashMap<>();


    public ContractReport(UUID id, JmsSendStatus ackStatus, LocalDateTime creationDate, Map<String, Object> report) {
        this.id = id;
        this.ackStatus = ackStatus;
        this.creationDate = creationDate;
        this.report = report;
    }

    public ContractReport(UUID id, LocalDateTime creationDate) {
        this.id = id;
        this.creationDate = creationDate;
    }

    public ContractReport() {
    }


    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JmsSendStatus getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(JmsSendStatus ackStatus) {
        this.ackStatus = ackStatus;
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
