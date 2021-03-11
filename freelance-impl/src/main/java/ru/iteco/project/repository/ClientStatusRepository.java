package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.ClientStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности ClientStatus
 */
@Repository
public interface ClientStatusRepository extends JpaRepository<ClientStatus, UUID>, JpaSpecificationExecutor<ClientStatus> {

    /**
     * Метод получения сущности статуса пользователя по его строковому представлению
     *
     * @param value - строковое представление статуса пользователя
     * @return - Объект Optional с сущностью статуса пользователя или с null, если статус пользователя с данным значением не существует
     */
    Optional<ClientStatus> findClientStatusByValue(String value);


    /**
     * Метод проверяет существование статуса клиента с переданным значением поля value
     *
     * @param value - наименование статуса клиента
     * @return true - статус клиента с переданным значением существует,
     * false - статус клиента с переданным значением статуса не существует
     */
    Boolean existsClientStatusByValue(String value);
}
