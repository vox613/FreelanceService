package ru.iteco.project.domain;

import ru.iteco.project.domain.converter.LocalDateTimeAttributeConverter;
import ru.iteco.project.domain.listener.AuditingListener;

import javax.persistence.Convert;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Суперкласс всех классов-сущностей, содержит общие для всех сущностей поля
 */
@MappedSuperclass
@EntityListeners(AuditingListener.class)
public abstract class CreateAtIdentified {

    /*** Время и дата создания записи */
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    LocalDateTime createdAt;

    /*** Время и дата последнего обновления записи */
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    LocalDateTime updatedAt;

    public CreateAtIdentified() {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
