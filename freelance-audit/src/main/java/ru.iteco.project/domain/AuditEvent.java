package ru.iteco.project.domain;

import ru.iteco.project.domain.converter.ObjectToJsonAttributeConverter;
import ru.iteco.project.enumaration.AuditEventType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Модель данных представляющая событие аудита
 */
@Entity
@Table(name = "audit")
@IdClass(AuditEvent.AuditEventId.class)
public class AuditEvent implements Serializable {

    /*** Уникальный идентификатор события аудита */
    @Id
    @Column(nullable = false)
    private UUID id;

    /*** Код события аудита */
    @Id
    @Column(name = "audit_code", nullable = false)
    private String auditCode;

    /*** Тип события аудита */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_event_type", nullable = false)
    private AuditEventType auditEventType;

    /*** Дата и время соответствующие событию START */
    @Column(name = "time_start")
    private LocalDateTime timeStart;

    /*** Дата и время соответствующие заключительному событию выполнения метода */
    @Column(name = "time_end")
    private LocalDateTime timeEnd;

    /*** Имя пользователя от которого выполняется данный запрос */
    @Column(name = "username", nullable = false)
    private String userName;

    /*** Параметры запроса */
    @Column(name = "params", columnDefinition = "json")
    @Convert(converter = ObjectToJsonAttributeConverter.class)
    private Map<String, Object> params = new HashMap<>();

    /*** Возвращаемое значение в результате выполнения метода */
    @Column(name = "return_value", columnDefinition = "json")
    @Convert(converter = ObjectToJsonAttributeConverter.class)
    private Map<String, Object> returnValue = new HashMap<>();


    public AuditEvent() {
    }

    public AuditEvent(UUID id, String auditCode, AuditEventType auditEventType, LocalDateTime timeStart,
                      LocalDateTime timeEnd, String userName, Map<String, Object> params, Map<String, Object> returnValue) {
        this.id = id;
        this.auditCode = auditCode;
        this.auditEventType = auditEventType;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.userName = userName;
        this.params = params;
        this.returnValue = returnValue;
    }


    /**
     * Класс для формирования составного ключа к сущности AuditEvent
     */
    public static class AuditEventId implements Serializable {

        /*** Уникальный идентификатор события аудита */
        private UUID id;

        /*** Тип события аудита */
        private AuditEventType auditEventType;


        public AuditEventId() {
        }

        public AuditEventId(UUID id, AuditEventType auditEventType) {
            this.id = id;
            this.auditEventType = auditEventType;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public AuditEventType getAuditEventType() {
            return auditEventType;
        }

        public void setAuditEventType(AuditEventType auditEventType) {
            this.auditEventType = auditEventType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuditEventId that = (AuditEventId) o;
            return id.equals(that.id) &&
                    auditEventType == that.auditEventType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, auditEventType);
        }
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(String auditCode) {
        this.auditCode = auditCode;
    }

    public AuditEventType getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(AuditEventType auditEventType) {
        this.auditEventType = auditEventType;
    }

    public LocalDateTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalDateTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDateTime getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(LocalDateTime timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Map<String, Object> returnValue) {
        this.returnValue = returnValue;
    }
}
