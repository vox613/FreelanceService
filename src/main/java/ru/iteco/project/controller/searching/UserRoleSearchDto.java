package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска ролей пользователей с переданными ограничениями по полям
 */
public class UserRoleSearchDto extends AbstractSearchDto {

    /*** Наименование роли пользователя */
    private SearchUnit value;

    /*** Дата и время создания роли пользователя */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления роли пользователя */
    private SearchUnit updatedAt;


    public UserRoleSearchDto() {
    }

    public SearchUnit getValue() {
        return value;
    }

    public void setValue(SearchUnit value) {
        this.value = value;
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
    public UserRoleSearchDto searchData() {
        return this;
    }
}
