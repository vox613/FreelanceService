package ru.iteco.project.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Класс предоставляет методы работы с механизмом обратных вызовов EntityManager'а
 */
public class AuditingListener {

    private static final Logger log = LoggerFactory.getLogger(AuditingListener.class.getName());

    /**
     * Метод вызывается в момент вызова метода persist()
     *
     * @param createAtIdentified - объект сущности для установки/обновления необходимых данных
     */
    @PrePersist
    public void prePersist(CreateAtIdentified createAtIdentified) {
        log.info("set createdAt & updatedAt values");
        createAtIdentified.setCreatedAt(LocalDateTime.now());
        createAtIdentified.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Метод вызывается перед сохранением изменений сущности в базу
     *
     * @param createAtIdentified - объект сущности для установки/обновления необходимых данных
     */
    @PreUpdate
    public void preUpdate(CreateAtIdentified createAtIdentified) {
        log.info("set updatedAt value");
        createAtIdentified.setUpdatedAt(LocalDateTime.now());
    }

}
