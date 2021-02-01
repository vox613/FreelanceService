package ru.iteco.project.logger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Утилитарный класс для логирования событий
 */
public class LoggerUtils {

    private static final Logger log = LogManager.getLogger(LoggerUtils.class);

    /*** Шаблон для логирования события до вызова метода**/
    private static final String BEFORE_CALL_PATTERN = "{} with {} - start";

    /*** Шаблон для логирования события после вызова метода**/
    private static final String AFTER_CALL_PATTERN = "{} end with result {}";

    /*** Объект маппера **/
    private static final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


    /**
     * Метод логирует данные перед вызовом определенного метода. Формируется стандарная строка лога с заполненными
     * названием и сериализованными аргументами выызываемого метода, лог отбрасывается с переданным уровнем.
     *
     * @param logLevel   - уровень логирования
     * @param methodName - название метода логирование вызова которого осуществляетя
     * @param data       - аргументы вызываемого метода
     */
    public static void beforeCall(Level logLevel, String methodName, Object... data) {
        logMethodCall(logLevel, BEFORE_CALL_PATTERN, methodName, data);
    }


    /**
     * Метод логирует данные после вызова определенного метода. Формируется стандарная строка лога с заполненными
     * названием и сериализованными аргументами выызываемого метода, лог отбрасывается с переданным уровнем.
     *
     * @param logLevel   - уровень логирования
     * @param methodName - название метода логирование вызова которого осуществляетя
     * @param data       - результат выполнения логируемого метода
     */
    public static void afterCall(Level logLevel, String methodName, Object... data) {
        logMethodCall(logLevel, AFTER_CALL_PATTERN, methodName, data);
    }


    public static void logMethodCall(Level logLevel, String pattern, String methodName, Object... data) {
        try {
            String asString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            log.atLevel(logLevel).log(pattern, methodName, asString);
        } catch (JsonProcessingException e) {
            log.error("Method args converting error!", e);
        }
    }

}
