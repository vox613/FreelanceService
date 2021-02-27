package ru.iteco.project.config.security;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.iteco.project.domain.User;
import ru.iteco.project.jwt.domain.CustomClaims;
import ru.iteco.project.jwt.service.JwtValidationService;
import ru.iteco.project.service.UserService;

import java.util.Optional;
import java.util.UUID;

/**
 * Класс-конфигурация менеджера аутентификации в Spring Security
 */
@Service
@PropertySource("classpath:application.yml")
public class TokenAuthenticationManager implements AuthenticationManager {

    /*** Объект окружения*/
    private final Environment environment;

    /*** Объект сервисного слоя для валидации JWT токена*/
    private final JwtValidationService validationService;

    /*** Объект сервисного слоя для работы с сущностями User*/
    private final UserService userService;

    public TokenAuthenticationManager(Environment environment, JwtValidationService validationService, UserService userService) {
        this.environment = environment;
        this.validationService = validationService;
        this.userService = userService;
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
        User byUsername = userService.findByUsername(claims.getSubject());

        UserPrincipal userPrincipal = UserPrincipal
                .builder()
                .id(UUID.fromString(claims.getUserId()))
                .username(claims.getSubject())
                .password(byUsername.getPassword())
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
