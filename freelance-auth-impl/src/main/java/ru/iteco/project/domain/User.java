package ru.iteco.project.domain;

import javax.persistence.*;
import java.util.UUID;

/**
 * Модель данных представляющая пользователей
 */
@Entity
@Table(schema = "freelance_auth", name = "users")
public class User extends CreateAtIdentified implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id пользователя */
    @Id
    @Column
    private UUID id;

    /*** Логин пользователя */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /*** Пароль пользователя */
    @Column(name = "password", nullable = false)
    private String password;

    /*** Email пользователя */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /*** Роль пользователя */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    /*** Статус пользователя */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;


    public User() {
    }

    public User(UUID id, String username, String password, String email, UserRole role, UserStatus status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
