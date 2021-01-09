package ru.iteco.project.controller.searching;


/**
 * Класс - dto для поиска заданий с переданными ограничениями по полям
 */
public class TaskSearchDto extends AbstractSearchDto {

    /*** Статус задания */
    private SearchUnit taskStatus;

    /*** Имя задания */
    private SearchUnit name;

    /*** Описание задания */
    private SearchUnit description;

    /*** Дата и время до наступления которой необходимо выполнить задание */
    private SearchUnit taskCompletionDate;

    /*** Стоимость исполнения задания */
    private SearchUnit price;

    /*** Дата и время создания пользователя */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления пользователя */
    private SearchUnit updatedAt;

    /*** Минимальная стоимость задания */
    private SearchUnit minPrice;

    /*** Максимальная стоимость задания */
    private SearchUnit maxPrice;


    public TaskSearchDto() {
    }

    public SearchUnit getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(SearchUnit taskStatus) {
        this.taskStatus = taskStatus;
    }

    public SearchUnit getName() {
        return name;
    }

    public void setName(SearchUnit name) {
        this.name = name;
    }

    public SearchUnit getDescription() {
        return description;
    }

    public void setDescription(SearchUnit description) {
        this.description = description;
    }

    public SearchUnit getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(SearchUnit taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public SearchUnit getPrice() {
        return price;
    }

    public void setPrice(SearchUnit price) {
        this.price = price;
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

    public SearchUnit getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(SearchUnit minPrice) {
        this.minPrice = minPrice;
    }

    public SearchUnit getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(SearchUnit maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public TaskSearchDto searchData() {
        return this;
    }
}
