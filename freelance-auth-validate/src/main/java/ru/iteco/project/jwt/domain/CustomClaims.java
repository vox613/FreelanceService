package ru.iteco.project.jwt.domain;

import io.jsonwebtoken.impl.DefaultClaims;

import java.util.Map;

/**
 * Контейнер расширяющий наполнение payload токена
 */
public class CustomClaims extends DefaultClaims {

    /*** Ключ для email пользователя*/
    private static final String EMAIL = "email";

    /*** Ключ для role пользователя*/
    private static final String ROLE = "role";


    public CustomClaims(Map<String, ?> map) {
        super(map);
    }


    public String getEmail() {
        return getString(EMAIL);
    }

    public void setEmail(String email) {
        setValue(EMAIL, email);
    }

    public String getRole() {
        return getString(ROLE);
    }

    public void setRole(String role) {
        setValue(ROLE, role);
    }
}
