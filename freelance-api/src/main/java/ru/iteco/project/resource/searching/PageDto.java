package ru.iteco.project.resource.searching;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(description = "Модель ответа для поиска по объекатам")
public class PageDto<T> implements Serializable {

    @ApiModelProperty(value = "Список объектов DTO - результатов поиска", required = true, allowEmptyValue = true)
    private List<T> data;

    @ApiModelProperty(value = "Общее количество результатов удовлетворяющих критериям поиска", example = "15",
            required = true)
    private long totalElements;

    @ApiModelProperty(value = "Общее количество страниц для отображения результатов текущего поиска", example = "20",
            required = true)
    private long totalPages;


    public PageDto() {
    }

    public PageDto(List<T> data, long totalElements, long totalPages) {
        this.data = data;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}
