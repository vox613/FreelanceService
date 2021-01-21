package ru.iteco.project.dto;

import ru.iteco.project.enumaration.AuditCode;
import ru.iteco.project.enumaration.AuditEventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Модель события аудита
 */
public class AuditEventDto implements Serializable {

    /*** Уникальный идентификатор события аудита */
    private UUID id;

    /*** Код события аудита */
    private AuditCode auditCode;

    /*** Тип события аудита */
    private AuditEventType auditEventType;

    /*** Дата и время соответствующие событию START */
    private LocalDateTime timeStart;

    /*** Дата и время соответствующие заключительному событию выполнения метода */
    private LocalDateTime timeEnd;

    /*** Имя пользователя от которого выполняется данный запрос */
    private String userName;

    /*** Параметры запроса */
    private Map<String, Object> params = new HashMap<>();

    /*** Возвращаемое значение в результате выполнения метода */
    private Map<String, Object> returnValue = new HashMap<>();


    public AuditEventDto(UUID id, AuditCode auditCode, AuditEventType auditEventType, LocalDateTime timeStart,
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

    public AuditEventDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AuditCode getAuditCode() {
        return auditCode;
    }

    public void setAuditCode(AuditCode auditCode) {
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
