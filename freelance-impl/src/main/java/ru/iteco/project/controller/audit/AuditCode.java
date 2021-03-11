package ru.iteco.project.controller.audit;

/**
 * Коды событий аудита
 */
public class AuditCode {

    /*** Создание клиента*/
    public static final String CLIENT_CREATE = "CLIENT_CREATE";
    /*** Обновление клиента*/
    public static final String CLIENT_UPDATE = "CLIENT_UPDATE";
    /*** Удаление клиента*/
    public static final String CLIENT_DELETE = "CLIENT_DELETE";
    /*** Изменение статуса клиента*/
    public static final String UPDATE_CLIENT_STATUS = "UPDATE_CLIENT_STATUS";


    /*** Создание роли клиента*/
    public static final String CLIENT_ROLE_CREATE = "CLIENT_ROLE_CREATE";
    /*** Обновление роли клиента*/
    public static final String CLIENT_ROLE_UPDATE = "CLIENT_ROLE_UPDATE";
    /*** Удаление роли клиента*/
    public static final String CLIENT_ROLE_DELETE = "CLIENT_ROLE_DELETE";

    /*** Создание статуса клиента*/
    public static final String CLIENT_STATUS_CREATE = "CLIENT_STATUS_CREATE";
    /*** Обновление статуса клиента*/
    public static final String CLIENT_STATUS_UPDATE = "CLIENT_STATUS_UPDATE";
    /*** Удаление статуса клиента*/
    public static final String CLIENT_STATUS_DELETE = "CLIENT_STATUS_DELETE";

    /*** Создание задания*/
    public static final String TASK_CREATE = "TASK_CREATE";
    /*** Обновление задания*/
    public static final String TASK_UPDATE = "TASK_UPDATE";
    /*** Удаление задания*/
    public static final String TASK_DELETE = "TASK_DELETE";

    /*** Создание статуса задания*/
    public static final String TASK_STATUS_CREATE = "TASK_STATUS_CREATE";
    /*** Обновление статуса задания*/
    public static final String TASK_STATUS_UPDATE = "TASK_STATUS_UPDATE";
    /*** Удаление статуса задания*/
    public static final String TASK_STATUS_DELETE = "TASK_STATUS_DELETE";

    /*** Создание контракта*/
    public static final String CONTRACT_CREATE = "CONTRACT_CREATE";
    /*** Обновление контракта*/
    public static final String CONTRACT_UPDATE = "CONTRACT_UPDATE";
    /*** Удаление контракта*/
    public static final String CONTRACT_DELETE = "CONTRACT_DELETE";

    /*** Создание статуса контракта*/
    public static final String CONTRACT_STATUS_CREATE = "CONTRACT_STATUS_CREATE";
    /*** Обновление статуса контракта*/
    public static final String CONTRACT_STATUS_UPDATE = "CONTRACT_STATUS_UPDATE";
    /*** Удаление статуса контракта*/
    public static final String CONTRACT_STATUS_DELETE = "CONTRACT_STATUS_DELETE";

}

