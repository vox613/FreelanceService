package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.controller.dto.UserRoleDtoRequest;
import ru.iteco.project.controller.dto.UserRoleDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserRoleSearchDto;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности UserRole
 */
public interface UserRoleService {

    /**
     * Метод получения роли пользователя по id
     *
     * @param id - уникальный идентификатор роли пользователя
     * @return - представление данных роли пользователя в форме UserRoleDtoResponse
     */
    UserRoleDtoResponse getUserRoleById(UUID id);

    /**
     * Метод создания новой роли пользователя
     *
     * @param userRoleDtoRequest - запрос с данными о роли пользователя
     * @return - объект UserRoleDtoResponse с уникальным идентификатором id
     */
    UserRoleDtoResponse createUserRole(UserRoleDtoRequest userRoleDtoRequest);

    /**
     * Метод обновления данных роли пользователя
     *
     * @param id                 - уникальный идентификатор роли пользователя
     * @param userRoleDtoRequest - запрос с обновленными данными клиента
     * @return - объект UserRoleDtoResponse с обновленной сущностью роли пользователя
     */
    UserRoleDtoResponse updateUserRole(UUID id, UserRoleDtoRequest userRoleDtoRequest);

    /**
     * Метод получения данных обо всех ролях пользователей
     *
     * @return - список всех ролей пользователей в форме UserRoleDtoResponse
     */
    ArrayList<UserRoleDtoResponse> getAllUsersRoles();

    /**
     * Метод удаляет роль пользователя
     *
     * @param id - id роли пользователя для удаления
     * @return - true - роль пользователя успешно удалена,
     * false - произошла ошибка при удалении роли пользователя или роли пользователя не существует
     */
    Boolean deleteUserRole(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<UserRoleDtoResponse> getRoles(SearchDto<UserRoleSearchDto> searchDto, Pageable pageable);

}


