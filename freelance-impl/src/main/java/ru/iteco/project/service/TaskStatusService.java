package ru.iteco.project.service;


import org.springframework.data.domain.Pageable;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.dto.TaskStatusDtoRequest;
import ru.iteco.project.resource.dto.TaskStatusDtoResponse;
import ru.iteco.project.resource.searching.TaskStatusSearchDto;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс описывает общий функционал Service слоя для сущности TaskStatus
 */
public interface TaskStatusService extends CommonService<TaskStatusDtoRequest, TaskStatus> {

    /**
     * Метод получения статуса задания по id
     *
     * @param id - уникальный идентификатор статуса задания
     * @return - представление данных статуса задания в форме TaskRoleDtoResponse
     */
    TaskStatusDtoResponse getTaskStatusById(UUID id);

    /**
     * Метод создания нового статуса задания
     *
     * @param taskStatusDtoRequest - запрос с данными о статусе задания
     * @return - объект TaskStatusDtoResponse с уникальным идентификатором id
     */
    TaskStatusDtoResponse createTaskStatus(TaskStatusDtoRequest taskStatusDtoRequest);

    /**
     * Метод обновления данных статуса задания
     *
     * @param id                   - уникальный идентификатор статуса задания
     * @param taskStatusDtoRequest - запрос с обновленными данными статуса
     * @return - объект TaskStatusDtoResponse с обновленной сущностью задания
     */
    TaskStatusDtoResponse updateTaskStatus(UUID id, TaskStatusDtoRequest taskStatusDtoRequest);

    /**
     * Метод получения данных обо всех статусах задания
     *
     * @return - список всех статусов задания в форме TaskRoleDtoResponse
     */
    List<TaskStatusDtoResponse> getAllTasksStatuses();

    /**
     * Метод удаляет статус задания
     *
     * @param id - id статуса задания для удаления
     * @return - true - статус задания успешно удалена,
     * false - произошла ошибка при удалении статуса задания или статуса задания не существует
     */
    Boolean deleteTaskStatus(UUID id);

    /**
     * Метод поиска данных на основании заданной пагинации и/или сортировки и критериев поиска
     *
     * @param searchDto - объект содержащий поля по которым осуществляется поиск данных
     * @param pageable  - объект пагинации и сортировки
     * @return - объект PageDto с результатами поиска данных по заданным критериям
     */
    PageDto<TaskStatusDtoResponse> getStatus(SearchDto<TaskStatusSearchDto> searchDto, Pageable pageable);

}
