package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности UserStatus, содержащего данные для отображения на фронте
 */
public class UserStatusDtoResponse extends UserStatusBaseDto {

    /*** Дата и время создания статуса пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления статуса пользователя */
    private String updatedAt;


    public UserStatusDtoResponse() {
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
