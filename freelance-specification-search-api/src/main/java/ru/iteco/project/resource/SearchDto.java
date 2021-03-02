package ru.iteco.project.resource;

import java.io.Serializable;

/**
 * Интерфейс для dto моделей поиска данных
 *
 * @param <T> - тип данных поиск которых осуществляется
 */
public interface SearchDto<T extends Serializable> extends Serializable {

    /**
     * Метод получения объекта поиска содержащего критерии поиска
     *
     * @return - объект поиска содержащий критерии поиска
     */
    T searchData();
}
