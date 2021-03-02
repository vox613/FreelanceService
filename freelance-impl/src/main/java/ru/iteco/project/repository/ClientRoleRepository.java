package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.ClientRole;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности ClientRole
 */
@Repository
public interface ClientRoleRepository extends JpaRepository<ClientRole, UUID>, JpaSpecificationExecutor<ClientRole> {

    /**
     * Метод получения сущности роли пользователя по ее строковому представлению
     *
     * @param value - строковое представление роли пользователя
     * @return - Объект Optional с сущностью роли пользователя или с null, если роль пользователя с данным значением не существует
     */
    Optional<ClientRole> findClientRoleByValue(String value);

}
