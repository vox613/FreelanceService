package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности UserRole, содержащего данные для отображения на фронте
 */
public class UserRoleDtoResponse extends UserRoleBaseDto {

    /*** Дата и время создания роли пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления роли пользователя */
    private String updatedAt;


    public UserRoleDtoResponse() {
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
