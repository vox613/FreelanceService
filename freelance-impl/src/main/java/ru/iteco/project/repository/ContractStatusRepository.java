package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.ContractStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности ContractStatus
 */
@Repository
public interface ContractStatusRepository extends JpaRepository<ContractStatus, UUID>, JpaSpecificationExecutor<ContractStatus> {

    /**
     * Метод получения сущности статуса контракта по его строковому представлению
     *
     * @param value - строковое представление статуса контракта
     * @return - Объект Optional с сущностью статуса контракта или с null, если статус контракта с данным значением не существует
     */
    Optional<ContractStatus> findContractStatusByValue(String value);


    /**
     * Метод проверяет существование статуса контракта с переданным значением поля value
     *
     * @param value - наименование статуса контракта
     * @return true - статус контракта с переданным значением существует,
     * false - статус контракта с переданным значением статуса не существует
     */
    Boolean existsContractStatusByValue(String value);
}
