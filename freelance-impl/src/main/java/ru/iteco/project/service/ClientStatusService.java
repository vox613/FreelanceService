package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.dto.ClientStatusDtoRequest;
import ru.iteco.project.resource.dto.ClientStatusDtoResponse;
import ru.iteco.project.resource.searching.ClientStatusSearchDto;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности ClientStatus
 */
public interface ClientStatusService extends CommonService<ClientStatusDtoRequest, ClientStatus> {

    /**
     * Метод получения статуса пользователя по id
     *
     * @param id - уникальный идентификатор статуса пользователя
     * @return - представление данных статуса пользователя в форме ClientRoleDtoResponse
     */
    ClientStatusDtoResponse getClientStatusById(UUID id);

    /**
     * Метод создания нового статуса пользователя
     *
     * @param clientStatusDtoRequest - запрос с данными о статусе пользователя
     * @return - объект ClientRoleDtoResponse с уникальным идентификатором id
     */
    ClientStatusDtoResponse createClientStatus(ClientStatusDtoRequest clientStatusDtoRequest);

    /**
     * Метод обновления данных статуса пользователя
     *
     * @param id                     - уникальный идентификатор статуса пользователя
     * @param clientStatusDtoRequest - запрос с обновленными данными статуса
     * @return - объект ClientStatusDtoResponse с обновленной сущностью статуса пользователя
     */
    ClientStatusDtoResponse updateClientStatus(UUID id, ClientStatusDtoRequest clientStatusDtoRequest);

    /**
     * Метод получения данных обо всех статусах пользователей
     *
     * @return - список всех статусов пользователей в форме ClientRoleDtoResponse
     */
    List<ClientStatusDtoResponse> getAllClientStatuses();

    /**
     * Метод удаляет статус пользователя
     *
     * @param id - id статуса пользователя для удаления
     * @return - true - статус пользователя успешно удален,
     * false - произошла ошибка при удалении статуса пользователя или статуса пользователя не существует
     */
    Boolean deleteClientStatus(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<ClientStatusDtoResponse> getStatus(SearchDto<ClientStatusSearchDto> searchDto, Pageable pageable);
}
