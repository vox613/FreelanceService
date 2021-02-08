package ru.iteco.project.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * Класс-конфигурация для взаимодействия с JMS
 */
@Configuration
@EnableJms
public class JMSConfig {
    private static final Logger log = LogManager.getLogger(JMSConfig.class);

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        log.info("init MessageConverter");
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
