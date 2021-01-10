package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Модель запроса для поиска статусов заданий")
public class TaskStatusSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование статуса задания", allowEmptyValue = true)
    private SearchUnit value;

    @ApiModelProperty(value = "Описание статуса задания", allowEmptyValue = true)
    private SearchUnit description;


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

    @Override
    public TaskStatusSearchDto searchData() {
        return this;
    }
}
