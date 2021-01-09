package ru.iteco.project.controller.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс для формирования dto объекта сущности User, содержащего данные для отображения на фронте
 */
public class UserDtoResponse extends UserBaseDto {

    /*** Дата и время создания пользователя */
    private String createdAt;

    /*** Дата и время последнего обновления пользователя */
    private String updatedAt;

    /*** Список id заданий пользователя */
    private final List<UUID> tasksIdList = new ArrayList<>();


    public UserDtoResponse() {
    }

    public List<UUID> getTasksIdList() {
        return tasksIdList;
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
