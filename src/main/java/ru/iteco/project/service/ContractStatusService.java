package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.controller.dto.ContractStatusDtoRequest;
import ru.iteco.project.controller.dto.ContractStatusDtoResponse;
import ru.iteco.project.controller.searching.ContractStatusSearchDto;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности ContractStatus
 */
public interface ContractStatusService {

    /**
     * Метод получения статуса контракта по id
     *
     * @param id - уникальный идентификатор статуса контракта
     * @return - представление данных статуса контракта в форме ContractStatusDtoResponse
     */
    ContractStatusDtoResponse getContractStatusById(UUID id);

    /**
     * Метод создания нового статуса контракта
     *
     * @param contractStatusDtoRequest - запрос с данными о статусе контракта
     * @return - объект ContractStatusDtoResponse с уникальным идентификатором id
     */
    ContractStatusDtoResponse createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest);

    /**
     * Метод обновления данных статуса контракта
     *
     * @param id                       - уникальный идентификатор статуса контракта
     * @param contractStatusDtoRequest - запрос с обновленными данными статуса
     * @return - объект ContractStatusDtoResponse с обновленной сущностью контракта
     */
    ContractStatusDtoResponse updateContractStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest);

    /**
     * Метод получения данных обо всех статусах контрактов
     *
     * @return - список всех статусов контрактов в форме ContractStatusDtoResponse
     */
    ArrayList<ContractStatusDtoResponse> getAllContractsStatuses();

    /**
     * Метод удаляет статус контракта
     *
     * @param id - id статуса контракта для удаления
     * @return - true - статус контракта успешно удален,
     * false - произошла ошибка при удалении статуса контракта или статуса контракта не существует
     */
    Boolean deleteContractStatus(UUID id);

    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<ContractStatusDtoResponse> getStatus(SearchDto<ContractStatusSearchDto> searchDto, Pageable pageable);
}
