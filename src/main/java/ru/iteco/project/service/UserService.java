package ru.iteco.project.service;

import org.springframework.data.domain.Pageable;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.UserSearchDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности User
 */
public interface UserService {

    /**
     * Метод получения пользователя по id
     *
     * @param id - уникальный идентификатор пользователя
     * @return - представление данных пользователя в форме UserDtoResponse
     */
    UserDtoResponse getUserById(UUID id);

    /**
     * Метод создания нового пользователя
     *
     * @param userDtoRequest - запрос с данными пользователя
     * @return - объект UserDtoRequest с уникальным идентификатором id
     */
    UserDtoResponse createUser(UserDtoRequest userDtoRequest);

    /**
     * Метод создания список пользователей
     *
     * @param userDtoRequestList - список пользователей
     * @return - список пользователей  с проставленными уникальными идентификатороми id
     */
    List<UserDtoResponse> createBundleUsers(List<UserDtoRequest> userDtoRequestList);

    /**
     * Метод обновления данны пользователя
     *
     * @param userDtoRequest - запрос с обновленными данными клиента
     * @return - объект UserDtoResponse с обновленной сущностью пользователя
     */
    UserDtoResponse updateUser(UserDtoRequest userDtoRequest);

    /**
     * Метод получения данных обо всех пользователях
     *
     * @return - список всех пользователей в форме UserDtoResponse
     */
    ArrayList<UserDtoResponse> getAllUsers();

    /**
     * Метод удаляет пользователя из коллекции
     *
     * @param id - id пользователя для удаления
     * @return - true - пользователь успешно удален,
     * false - произошла ошибка при удалении пользователя/полььзователя не существует
     */
    Boolean deleteUser(UUID id);


    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<UserDtoResponse> getUsers(SearchDto<UserSearchDto> searchDto, Pageable pageable);
}
