package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска статусов пользователей с переданными ограничениями по полям
 */
public class UserStatusSearchDto extends AbstractSearchDto {

    /*** Наименование статуса пользователя */
    private SearchUnit value;

    /*** Описание статуса пользователя */
    private SearchUnit description;

    /*** Дата и время создания роли пользователя */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления роли пользователя */
    private SearchUnit updatedAt;


    public UserStatusSearchDto() {
    }

    public SearchUnit getValue() {
        return value;
    }

    public void setValue(SearchUnit value) {
        this.value = value;
    }

    public SearchUnit getDescription() {
        return description;
    }

    public void setDescription(SearchUnit description) {
        this.description = description;
    }

    public SearchUnit getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(SearchUnit createdAt) {
        this.createdAt = createdAt;
    }

    public SearchUnit getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(SearchUnit updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public UserStatusSearchDto searchData() {
        return this;
    }
}
