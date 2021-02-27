package ru.iteco.project.config.security;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Класс-контейнер данных пользователя для Security
 */
public class UserPrincipal implements UserDetails {

    /*** Множество прав пользователя*/
    private final Set<GrantedAuthority> authorities;

    /*** Уникальный идентификатор пользователя*/
    private final UUID userId;

    /*** Уникальный идентификатор пользователя*/
    private final String password;

    /*** Логин пользователя*/
    private final String username;

    /*** Не просрочен ли аккаунт пользователя*/
    private final boolean isAccountNonExpired;

    /*** Не заблокирован ли аккаунт пользователя*/
    private final boolean isAccountNonLocked;

    /*** Не просрочены ли права пользователя*/
    private final boolean isCredentialsNonExpired;

    /*** Активирован ли пользователь*/
    private final boolean isEnabled;

    private UserPrincipal(Set<GrantedAuthority> authorities, UUID userId, String password, String username,
                          boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired,
                          boolean isEnabled) {
        this.authorities = authorities;
        this.userId = userId;
        this.password = password;
        this.username = username;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public static UserPrincipalBuilder builder() {
        return new UserPrincipalBuilder();
    }

    /**
     * Builder для класса UserPrincipal
     */
    public static class UserPrincipalBuilder {

        private Set<GrantedAuthority> authorities;
        private UUID id;
        private String password;
        private String username;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean enabled;

        public UserPrincipalBuilder roles(String... roles) {
            if (!Objects.isNull(roles)) {
                Set<GrantedAuthority> authoritiesSet = new HashSet<>(roles.length);
                for (String role : roles) {
                    Assert.isTrue(!role.startsWith("ROLE_"),
                            String.format("%s cannot starts with 'ROLE_', it is added automatically", role));
                    authoritiesSet.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                this.authorities = authoritiesSet;
            }
            return this;
        }

        public UserPrincipalBuilder id(UUID id) {
            Assert.isTrue(!ObjectUtils.isEmpty(id), "Empty user id");
            this.id = id;
            return this;
        }

        public UserPrincipalBuilder password(String password) {
            Assert.isTrue(!ObjectUtils.isEmpty(password), "Empty user password");
            this.password = password;
            return this;
        }

        public UserPrincipalBuilder username(String username) {
            Assert.isTrue(!ObjectUtils.isEmpty(username), "Empty user username");
            this.username = username;
            return this;
        }

        public UserPrincipalBuilder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public UserPrincipalBuilder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public UserPrincipalBuilder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public UserPrincipalBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserPrincipal build() {
            return new UserPrincipal(this.authorities, this.id, this.password, this.username, this.accountNonExpired,
                    this.accountNonLocked, this.credentialsNonExpired, this.enabled);
        }
    }
}
