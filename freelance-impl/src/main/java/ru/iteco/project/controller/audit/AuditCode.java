package ru.iteco.project.controller.audit;

/**
 * Коды событий аудита
 */
public class AuditCode {

    /*** Создание пользователя*/
    public static final String USER_CREATE = "USER_CREATE";
    /*** Создание пакета пользователей*/
    public static final String USER_BATCH_CREATE = "USER_BATCH_CREATE";
    /*** Обновление пользователя*/
    public static final String USER_UPDATE = "USER_UPDATE";
    /*** Удаление пользователя*/
    public static final String USER_DELETE = "USER_DELETE";


    /*** Создание роли пользователя*/
    public static final String USER_ROLE_CREATE = "USER_ROLE_CREATE";
    /*** Обновление роли пользователя*/
    public static final String USER_ROLE_UPDATE = "USER_ROLE_UPDATE";
    /*** Удаление роли пользователя*/
    public static final String USER_ROLE_DELETE = "USER_ROLE_DELETE";

    /*** Создание статуса пользователя*/
    public static final String USER_STATUS_CREATE = "USER_STATUS_CREATE";
    /*** Обновление статуса пользователя*/
    public static final String USER_STATUS_UPDATE = "USER_STATUS_UPDATE";
    /*** Удаление статуса пользователя*/
    public static final String USER_STATUS_DELETE = "USER_STATUS_DELETE";

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

