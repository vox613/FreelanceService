package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.UserRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности UserRole
 */
public interface UserRoleRepository extends JpaRepository<UserRole, UUID>, JpaSpecificationExecutor<UserRole> {

    /**
     * Метод получения сущности роли пользователя по ее строковому представлению
     *
     * @param value - строковое представление роли пользователя
     * @return - Объект Optional с сущностью роли пользователя или с null, если роль пользователя с данным значением не существует
     */
    Optional<UserRole> findUserRoleByValue(String value);

}
