package ru.iteco.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Утилитарный класс-конфигурация для предоставления необходимых ресурсов
 */
@Configuration
public class UtilsConfig {

    /**
     * Объявления бина clock
     *
     * @return экземпляр Clock использующий наилучшие из доступных системных часов с временной зоной по умолчанию
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
