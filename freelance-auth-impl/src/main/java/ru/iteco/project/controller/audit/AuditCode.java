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
    /*** Получение токена*/
    public static final String TOKEN_CREATE = "TOKEN_CREATE";

}

