package ru.iteco.project.service;

import org.springframework.data.domain.Pageable;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ClientDtoResponse;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.searching.ClientSearchDto;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности Client
 */
public interface ClientService {

    /**
     * Метод получения пользователя по id
     *
     * @param id - уникальный идентификатор пользователя
     * @return - представление данных пользователя в форме ClientDtoResponse
     */
    ClientDtoResponse getClientById(UUID id);

    /**
     * Метод создания нового пользователя
     *
     * @param clientDtoRequest - запрос с данными пользователя
     * @return - объект ClientDtoRequest с уникальным идентификатором id
     */
    ClientDtoResponse createClient(ClientDtoRequest clientDtoRequest);

    /**
     * Метод создания список пользователей
     *
     * @param clientDtoRequestList - список пользователей
     * @return - список пользователей  с проставленными уникальными идентификатороми id
     */
    List<ClientDtoResponse> createBundleClients(List<ClientDtoRequest> clientDtoRequestList);

    /**
     * Метод обновления данны пользователя
     *
     * @param clientDtoRequest - запрос с обновленными данными клиента
     * @return - объект ClientDtoResponse с обновленной сущностью пользователя
     */
    ClientDtoResponse updateClient(ClientDtoRequest clientDtoRequest);

    /**
     * Метод получения данных обо всех пользователях
     *
     * @return - список всех пользователей в форме ClientDtoResponse
     */
    List<ClientDtoResponse> getAllClients();

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param id - id пользователя для удаления
     * @return - true - пользователь успешно удален,
     * false - произошла ошибка при удалении пользователя/полььзователя не существует
     */
    Boolean deleteClient(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<ClientDtoResponse> getClients(SearchDto<ClientSearchDto> searchDto, Pageable pageable);
}
