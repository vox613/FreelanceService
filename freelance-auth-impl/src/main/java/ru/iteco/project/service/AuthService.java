package ru.iteco.project.service;

import ru.iteco.project.resource.dto.AuthUserDto;
import ru.iteco.project.resource.dto.TokenDto;

/**
 * Интерфейс описывает общий функционал для работы с процессом аутентификации
 */
public interface AuthService {

    /**
     * Метод генерирует токен аутентификации
     *
     * @param authUserDto - персональные данные клиента для прохождения аутентификации в системе
     * @return - аутентификационный токен пользователя
     */
    TokenDto generateToken(AuthUserDto authUserDto);
}
