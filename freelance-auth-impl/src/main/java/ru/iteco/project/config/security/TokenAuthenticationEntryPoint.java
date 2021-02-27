package ru.iteco.project.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.iteco.project.resource.dto.ResponseError;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Класс-конфигурация обработчика ошибок процесса аутентификации
 */
@Component
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LogManager.getLogger(TokenAuthenticationEntryPoint.class);

    @Value("${spring.application.name}")
    private String systemId;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        log.error(authenticationException.getMessage());
        ResponseError responseError = new ResponseError(
                UUID.randomUUID(),
                authenticationException.getMessage(),
                String.valueOf(HttpServletResponse.SC_UNAUTHORIZED),
                systemId
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getOutputStream().println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseError));
    }
}
