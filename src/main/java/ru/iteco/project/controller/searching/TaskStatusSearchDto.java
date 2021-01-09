package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска статусов заданий с переданными ограничениями по полям
 */
public class TaskStatusSearchDto extends AbstractSearchDto {

    /*** Наименование статуса задания */
    private SearchUnit value;

    /*** Описание статуса задания */
    private SearchUnit description;

    /*** Дата и время создания статуса задания */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления статуса задания */
    private SearchUnit updatedAt;


    public TaskStatusSearchDto() {
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
    public TaskStatusSearchDto searchData() {
        return this;
    }
}
