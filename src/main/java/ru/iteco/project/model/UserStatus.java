package ru.iteco.project.model;

/**
 * Перечисление возможных статусов в которых может находитья пользователь
 */
public enum UserStatus {

    NOT_EXIST("Пользователя не существует"),
    STATUS_CREATED("Создан"),
    STATUS_LOCKED("Заблокирован"),
    STATUS_ACTIVE("Активен");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}