package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.controller.dto.UserStatusDtoRequest;
import ru.iteco.project.controller.dto.UserStatusDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserStatusSearchDto;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности UserStatus
 */
public interface UserStatusService {

    /**
     * Метод получения статуса пользователя по id
     *
     * @param id - уникальный идентификатор статуса пользователя
     * @return - представление данных статуса пользователя в форме UserRoleDtoResponse
     */
    UserStatusDtoResponse getUserStatusById(UUID id);

    /**
     * Метод создания нового статуса пользователя
     *
     * @param userStatusDtoRequest - запрос с данными о статусе пользователя
     * @return - объект UserRoleDtoResponse с уникальным идентификатором id
     */
    UserStatusDtoResponse createUserStatus(UserStatusDtoRequest userStatusDtoRequest);

    /**
     * Метод обновления данных статуса пользователя
     *
     * @param id                   - уникальный идентификатор статуса пользователя
     * @param userStatusDtoRequest - запрос с обновленными данными статуса
     * @return - объект UserStatusDtoResponse с обновленной сущностью статуса пользователя
     */
    UserStatusDtoResponse updateUserStatus(UUID id, UserStatusDtoRequest userStatusDtoRequest);

    /**
     * Метод получения данных обо всех статусах пользователей
     *
     * @return - список всех статусов пользователей в форме UserRoleDtoResponse
     */
    ArrayList<UserStatusDtoResponse> getAllUsersStatuses();

    /**
     * Метод удаляет статус пользователя
     *
     * @param id - id статуса пользователя для удаления
     * @return - true - статус пользователя успешно удален,
     * false - произошла ошибка при удалении статуса пользователя или статуса пользователя не существует
     */
    Boolean deleteUserStatus(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<UserStatusDtoResponse> getStatus(SearchDto<UserStatusSearchDto> searchDto, Pageable pageable);
}
