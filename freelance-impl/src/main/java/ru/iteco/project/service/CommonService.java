package ru.iteco.project.service;

import ru.iteco.project.domain.Identified;
import ru.iteco.project.resource.dto.DtoInterface;

import java.util.UUID;

/**
 * Интерфейс содержит сигнатуры общих методов для всего сервисного слоя приложения
 *
 * @param <T> - тип входящей модели DTO запроса
 * @param <S> - тип сущности обновление данных которой осуществляется
 */
public interface CommonService<T extends DtoInterface, S extends Identified<UUID>> {

    /**
     * Метод осуществляет проверку возможности создания новой записи (например проверки уникальности значений
     * полей полученной модели относительно существующих записей, и т.д.)
     *
     * @param t - модель запроса для осуществления необходимых проверок
     */
    void checkPossibilityToCreate(T t);

    /**
     * Метод осуществляет проверку возможности обновления записи (например проверки уникальности значений
     * полей полученной модели относительно существующих записей, и т.д.)
     *
     * @param t - модель запроса для осуществления необходимых проверок
     * @param s - сущность, поля которой обновляются в результате запроса
     */
    void checkUpdatedData(T t, S s);

}
