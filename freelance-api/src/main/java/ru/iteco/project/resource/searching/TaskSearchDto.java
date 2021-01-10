package ru.iteco.project.resource.searching;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Модель запроса для поиска заданий")
public class TaskSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Статус задания", allowEmptyValue = true)
    private SearchUnit taskStatus;

    @ApiModelProperty(value = "Название задания", allowEmptyValue = true)
    private SearchUnit title;

    @ApiModelProperty(value = "Описание задания", allowEmptyValue = true)
    private SearchUnit description;

    @ApiModelProperty(value = "Дата и время до наступления которой необходимо выполнить задание", allowEmptyValue = true)
    private SearchUnit taskCompletionDate;

    @ApiModelProperty(value = "Стоимость исполнения задания", allowEmptyValue = true)
    private SearchUnit price;


    public TaskSearchDto() {
    }

    public SearchUnit getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(SearchUnit taskStatus) {
        this.taskStatus = taskStatus;
    }

    public SearchUnit getTitle() {
        return title;
    }

    public void setTitle(SearchUnit title) {
        this.title = title;
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

    @Override
    public TaskSearchDto searchData() {
        return this;
    }
}
