package ru.iteco.project.config.security;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.jwt.domain.CustomClaims;
import ru.iteco.project.jwt.service.JwtValidationService;

import java.util.Optional;
import java.util.UUID;

/**
 * Класс-конфигурация менеджера аутентификации в Spring Security
 */
@Service
public class TokenAuthenticationManager implements AuthenticationManager {

    /*** Объект окружения*/
    private final Environment environment;

    /*** Объект сервисного слоя для валидации JWT токена*/
    private final JwtValidationService validationService;


    public TokenAuthenticationManager(Environment environment, JwtValidationService validationService) {
        this.validationService = validationService;
        this.environment = environment;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = Optional.ofNullable(String.valueOf(authentication.getCredentials())).orElse(null);
        if (ObjectUtils.isEmpty(token)) {
            authentication.setAuthenticated(false);
            return authentication;
        }
        String secret = environment.getProperty("authentication.jwt.secret");
        String applicationName = environment.getProperty("spring.application.name");

        CustomClaims claims = validationService.getTokenClaims(token, secret);
        validationService.validateToken(claims, secret, applicationName);
        UserPrincipal userPrincipal = UserPrincipal
                .builder()
                .id(UUID.fromString(claims.getUserId()))
                .username(claims.getSubject())
                .password("PROTECTED")
                .roles(claims.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        TokenAuthentication tokenAuthentication = new TokenAuthentication(userPrincipal, token);
        tokenAuthentication.setAuthenticated(true);
        return tokenAuthentication;
    }
}
