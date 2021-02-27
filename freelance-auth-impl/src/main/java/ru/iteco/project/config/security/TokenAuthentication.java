package ru.iteco.project.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Класс-описание объекта аутентификации
 */
public class TokenAuthentication implements Authentication {

    /*** Информация о пользователе*/
    private final UserDetails principal;

    /*** Признак аутентификации пользователя*/
    private boolean authenticated;

    /*** Токен jwt*/
    private final String token;


    public TokenAuthentication(UserDetails principal, String token) {
        this.principal = principal;
        this.token = token;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal != null ? principal.getUsername() : null;
    }

    public String getToken() {
        return token;
    }
}
