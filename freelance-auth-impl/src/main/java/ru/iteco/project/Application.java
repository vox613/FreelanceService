package ru.iteco.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Точка запуска String приложения
 */
@SpringBootApplication
@Configuration
@EnableTransactionManagement
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);

        BuildProperties buildProperties = application.getBean(BuildProperties.class);
        ConfigurableEnvironment env = application.getEnvironment();
        StringBuilder sb = new StringBuilder("\n----------------------------------------------------------\n\t");
        sb.append("Application: ").append(env.getProperty("spring.application.name")).append("\n\t");
        sb.append("Build version is ").append(buildProperties.getVersion()).append("\n\t");
        sb.append("Profile(s): ");
        sb.append(Arrays.stream(env.getActiveProfiles()).collect(Collectors.joining(", ")));
        sb.append(" \n").append("----------------------------------------------------------");
        log.info(sb.toString());
    }
}
