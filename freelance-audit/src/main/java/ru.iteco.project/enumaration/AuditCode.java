package ru.iteco.project.enumaration;

/**
 * Коды событий аудита
 */
public enum AuditCode {

    USER_CREATE("Создание пользователя"),
    USER_BATCH_CREATE("Создание пакета пользователей"),
    USER_UPDATE("Обновление пользователя"),
    USER_DELETE("Удаление пользователя"),

    USER_ROLE_CREATE("Создание роли пользователя"),
    USER_ROLE_UPDATE("Обновление роли пользователя"),
    USER_ROLE_DELETE("Удаление роли пользователя"),

    USER_STATUS_CREATE("Создание статуса пользователя"),
    USER_STATUS_UPDATE("Обновление статуса пользователя"),
    USER_STATUS_DELETE("Удаление статуса пользователя"),

    TASK_CREATE("Создание задания"),
    TASK_UPDATE("Обновление задания"),
    TASK_DELETE("Удаление задания"),

    TASK_STATUS_CREATE("Создание статуса задания"),
    TASK_STATUS_UPDATE("Обновление статуса задания"),
    TASK_STATUS_DELETE("Удаление статуса задания"),

    TASK_SCHEDULER_DELETE("Удаление неактуального просроченного задания"),

    CONTRACT_CREATE("Создание контракта"),
    CONTRACT_UPDATE("Обновление контракта"),
    CONTRACT_DELETE("Удаление контракта"),

    CONTRACT_STATUS_CREATE("Создание статуса контракта"),
    CONTRACT_STATUS_UPDATE("Обновление статуса контракта"),
    CONTRACT_STATUS_DELETE("Удаление статуса контракта");


    private final String description;

    AuditCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

