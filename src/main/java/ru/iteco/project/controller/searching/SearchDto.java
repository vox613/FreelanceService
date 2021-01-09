package ru.iteco.project.controller.searching;

import ru.iteco.project.controller.dto.DtoInterface;

/**
 * Интерфейс для dto моделей поиска данных
 *
 * @param <T> - тип данных поиск которых осуществляется
 */
public interface SearchDto<T extends DtoInterface> extends DtoInterface {

    /**
     * Метод получения объекта поиска содержащего критерии поиска
     *
     * @return - объект поиска содержащий критерии поиска
     */
    T searchData();
}
