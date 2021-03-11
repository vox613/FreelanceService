package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.Client;
import ru.iteco.project.domain.ClientRole;
import ru.iteco.project.domain.ClientStatus;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности Client
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, UUID>, JpaSpecificationExecutor<Client> {

    /**
     * Метод определяет существование клиента с переданным email
     *
     * @param email - электронная почта клиента
     * @return - true - клиент с такой почтой существует, false - клиента с данной почтой не существует.
     */
    boolean existsByEmail(String email);

    /**
     * Метод определяет существование клиента с переданным phoneNumber
     *
     * @param phoneNumber - телефон клиента
     * @return - true - клиент с таким телефоном существует, false - клиента с данным телефонным номером не существует.
     */
    boolean existsByPhoneNumber(String phoneNumber);

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


    /**
     * Метод определяет существование клиента с переданным id
     *
     * @param id - id клиента
     * @return - true - клиент с данным id  существует, false - клиента с данным id не существует.
     */
    boolean existsById(UUID id);

}
