package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности TaskStatus, содержащего данные для отображения на фронте
 */
public class TaskStatusDtoResponse extends TaskStatusBaseDto {

    /*** Дата и время создания статуса задания */
    private String createdAt;

    /*** Дата и время последнего обновления статуса задания */
    private String updatedAt;

    public TaskStatusDtoResponse() {
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
