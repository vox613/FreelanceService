package ru.iteco.project.controller.searching;


import java.io.Serializable;
import java.util.List;

/**
 * Модель страницы-ответа на запрос поиска с пагинацией/сортировкой
 *
 * @param <T> - тип данных поиск которых осуществляется
 */
public class PageDto<T> implements Serializable {

    /*** Список результатов поиска*/
    private List<T> data;

    /*** Общее количество результатов удовлетворяющих критериям поиска*/
    private long totalElements;

    /*** Общее количество страниц для отображения результатов текущего поиска*/
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
