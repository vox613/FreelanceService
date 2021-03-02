package ru.iteco.project.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Класс-конфигурация для фильтра безопасности Spring Security
 */
public class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    protected TokenAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getRequestDispatcher(request.getServletPath()).forward(request, response);
        });
        setAuthenticationFailureHandler(((request, response, exception) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage())
        ));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(token -> token.startsWith("Bearer "))
                .map(token -> token.substring(7))
                .orElseThrow(() -> new AuthenticationServiceException("Invalid token"));

        Authentication authentication = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(jwtToken, jwtToken));
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationServiceException("Invalid token");
        }
        return authentication;
    }
}
