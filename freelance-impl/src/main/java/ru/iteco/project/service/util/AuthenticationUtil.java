package ru.iteco.project.service.util;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.iteco.project.config.security.UserPrincipal;

import java.util.UUID;

/**
 * Утилитарный класс для взаимодейставия с данными аутентифицированного пользователя
 */
public class AuthenticationUtil {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";


    /**
     * Метод получения уникального id  пользователя закрепленного за ним в системе аутентификации.
     *
     * @return - id пользователя или кидает IllegalArgumentException при отсутствии значения
     */
    public static UUID getUserPrincipalId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        UUID userId = principal.getUserId();
        if (ObjectUtils.isEmpty(userId)) {
            throw new IllegalArgumentException("errors.arguments.illegal");
        }
        return userId;
    }

    /**
     * Метод определяет имеет ли пользователь переданную роль
     *
     * @param role - роль пользователя
     * @return true - пользователь обладает переданной ролью, false - пользователь не обладает указанной ролью.
     */
    public static boolean userHasRole(final String role) {
        if (ObjectUtils.isEmpty(role)) {
            throw new IllegalArgumentException("errors.arguments.illegal");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> role.equals(grantedAuthority.toString()));

    }

    /**
     * Метод сравнивает соответствие переданного clientId полученному в объекте аутентификации userId, в случае
     * несоответствия выбрасывается AccessDeniedException
     *
     * @param clientId - id клиента полученный из сущности
     */
    public static void userIdAndClientIdIsMatched(UUID clientId) {
        if (ObjectUtils.isEmpty(clientId)) {
            throw new IllegalArgumentException("errors.arguments.illegal");
        }

        if (!clientId.equals(getUserPrincipalId())) {
            throw new AccessDeniedException("errors.operation.forbidden");
        }
    }

}
