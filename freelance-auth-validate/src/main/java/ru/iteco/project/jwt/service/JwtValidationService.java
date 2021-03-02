package ru.iteco.project.jwt.service;

import ru.iteco.project.jwt.domain.CustomClaims;

/**
 * Интерфейс описывающий методы дл валидации jwt токена
 */
public interface JwtValidationService {

    /**
     * Метод парсит полученный токен и возвращает claims из payload данного токена
     *
     * @param token  - входное значение токена
     * @param secret - секрет для дешифровки токена
     * @return - Claims для переданного токена
     */
    CustomClaims getTokenClaims(String token, String secret);

    /**
     * Метод осуществляет валидацию токена
     *
     * @param token - токен для валидации
     * @return - набор Claims - провалидированных атрибутов
     */
    CustomClaims validateToken(String token, String secret, String applicationName);

}
