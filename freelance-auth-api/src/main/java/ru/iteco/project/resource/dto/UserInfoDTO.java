package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "Модель с информацией об аутентификации пользователя")
public class UserInfoDTO {

    @ApiModelProperty(value = "Логин пользователя", example = "username", required = true)
    private final String username;

    @ApiModelProperty(value = "Признак не просроченного аккаунта", example = "true", required = true)
    private final boolean isAccountNonExpired;

    @ApiModelProperty(value = "Признак не заблокированного аккаунта", example = "true", required = true)
    private final boolean isAccountNonLocked;

    @ApiModelProperty(value = "Признак не просроченного пароля", example = "true", required = true)
    private final boolean isCredentialsNonExpired;

    @ApiModelProperty(value = "Признак активного пользователя", example = "true", required = true)
    private final boolean isEnabled;

    @ApiModelProperty(value = "Признак аутентифицированного пользователя", example = "true", required = true)
    private final boolean authenticated;

    @ApiModelProperty(value = "Предоставленные роли пользователя", required = true)
    private final List<Object> authorities;

    public UserInfoDTO(String username, boolean isAccountNonExpired, boolean isAccountNonLocked,
                       boolean isCredentialsNonExpired, boolean isEnabled,
                       boolean authenticated, List<Object> authorities) {
        this.username = username;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public List<Object> getAuthorities() {
        return authorities;
    }

    public static UserInfoBuilder builder() {
        return new UserInfoBuilder();
    }

    public static class UserInfoBuilder {

        private String username;
        private boolean isAccountNonExpired;
        private boolean isAccountNonLocked;
        private boolean isCredentialsNonExpired;
        private boolean isEnabled;
        private boolean authenticated;
        private List<Object> authorities = new ArrayList<>();

        public UserInfoBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public UserInfoBuilder setAccountNonExpired(boolean accountNonExpired) {
            isAccountNonExpired = accountNonExpired;
            return this;
        }

        public UserInfoBuilder setAccountNonLocked(boolean accountNonLocked) {
            isAccountNonLocked = accountNonLocked;
            return this;
        }

        public UserInfoBuilder setCredentialsNonExpired(boolean credentialsNonExpired) {
            isCredentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public UserInfoBuilder setEnabled(boolean enabled) {
            isEnabled = enabled;
            return this;
        }

        public UserInfoBuilder setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public UserInfoBuilder setAuthorities(List<Object> authorities) {
            this.authorities = authorities;
            return this;
        }

        public UserInfoDTO build() {
            return new UserInfoDTO(this.username, this.isAccountNonExpired, this.isAccountNonLocked,
                    this.isCredentialsNonExpired, this.isEnabled, this.authenticated, this.authorities);
        }
    }
}
