package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.resource.dto.ClientRoleDtoRequest;
import ru.iteco.project.resource.dto.ClientRoleDtoResponse;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.searching.ClientRoleSearchDto;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности ClientRole
 */
public interface ClientRoleService {

    /**
     * Метод получения роли пользователя по id
     *
     * @param id - уникальный идентификатор роли пользователя
     * @return - представление данных роли пользователя в форме ClientRoleDtoResponse
     */
    ClientRoleDtoResponse getClientRoleById(UUID id);

    /**
     * Метод создания новой роли пользователя
     *
     * @param clientRoleDtoRequest - запрос с данными о роли пользователя
     * @return - объект ClientRoleDtoResponse с уникальным идентификатором id
     */
    ClientRoleDtoResponse createClientRole(ClientRoleDtoRequest clientRoleDtoRequest);

    /**
     * Метод обновления данных роли пользователя
     *
     * @param id                 - уникальный идентификатор роли пользователя
     * @param clientRoleDtoRequest - запрос с обновленными данными клиента
     * @return - объект ClientRoleDtoResponse с обновленной сущностью роли пользователя
     */
    ClientRoleDtoResponse updateClientRole(UUID id, ClientRoleDtoRequest clientRoleDtoRequest);

    /**
     * Метод получения данных обо всех ролях пользователей
     *
     * @return - список всех ролей пользователей в форме ClientRoleDtoResponse
     */
    List<ClientRoleDtoResponse> getAllClientsRoles();

    /**
     * Метод удаляет роль пользователя
     *
     * @param id - id роли пользователя для удаления
     * @return - true - роль пользователя успешно удалена,
     * false - произошла ошибка при удалении роли пользователя или роли пользователя не существует
     */
    Boolean deleteClientRole(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<ClientRoleDtoResponse> getRoles(SearchDto<ClientRoleSearchDto> searchDto, Pageable pageable);

}


