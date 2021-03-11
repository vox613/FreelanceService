package ru.iteco.project.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.iteco.project.jwt.service.JwtValidationService;
import ru.iteco.project.security.JwtAccessDeniedHandler;
import ru.iteco.project.security.TokenAuthenticationEntryPoint;
import ru.iteco.project.security.TokenAuthenticationFilter;
import ru.iteco.project.security.TokenAuthenticationManager;

/**
 * Класс-конфигурация Spring Security
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /*** Объект окружения*/
    private final Environment environment;

    /*** Объект сервисного слоя для валидации JWT токена*/
    private final JwtValidationService jwtValidationService;

    /*** Объект хэндлера для события AccessDenied*/
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /*** Объект обработчик ошибки*/
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;


    public SecurityConfig(Environment environment, JwtValidationService jwtValidationService, JwtAccessDeniedHandler jwtAccessDeniedHandler,
                          TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint) {
        this.environment = environment;
        this.jwtValidationService = jwtValidationService;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.tokenAuthenticationEntryPoint = tokenAuthenticationEntryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers(
                        "/actuator/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/documentation/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
                .and().addFilterAt(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .and().exceptionHandling()
                .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);
    }

    public TokenAuthenticationFilter authenticationFilter() {
        TokenAuthenticationFilter filter = new TokenAuthenticationFilter("/api/v1/**");
        filter.setAuthenticationManager(new TokenAuthenticationManager(environment, jwtValidationService));
        return filter;
    }
}
