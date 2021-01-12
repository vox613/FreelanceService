package ru.iteco.project.enumaration;

/**
 * Типы событий аудита
 */
public enum AuditEventType {

    START("Начало выполнения метода"),
    SUCCESS("Успешное окончание выполнения метода"),
    FAILURE("Не успешное окончание выполнения метода");

    private final String description;

    AuditEventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
