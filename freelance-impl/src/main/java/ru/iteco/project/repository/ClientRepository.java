package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.Client;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.domain.ClientRole;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности Client
 */
public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {

    boolean existsByEmail(String email);

    /**
     * Метод получения всех пользователей с переданным статусом
     *
     * @param clientStatus - статус пользователя
     * @return - коллекция пользователей, с переданным статусом
     */
    Collection<Client> findAllByClientStatus(ClientStatus clientStatus);

    /**
     * Метод получения всех пользователей с переданной ролью
     *
     * @param clientRole - роль пользователя
     * @return - коллекция пользователей, с переданной ролью
     */
    Collection<Client> findAllByClientRole(ClientRole clientRole);

}
