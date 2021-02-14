package ru.iteco.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Класс конфигурация для  бина ReloadableResourceBundleMessageSource
 */
@Configuration
public class BundleMessageSourceConfig {

    /**
     * Метод создания бина messageSource для обеспечения работы с messageBundle
     * @return - настроенный объект ReloadableResourceBundleMessageSource
     */
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:message");

        messageSource.setUseCodeAsDefaultMessage(false);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
