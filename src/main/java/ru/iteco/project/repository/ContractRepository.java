package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.ContractStatus;
import ru.iteco.project.domain.Task;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности Contract
 */
public interface ContractRepository extends JpaRepository<Contract, UUID>, JpaSpecificationExecutor<Contract> {

    /**
     * Метод получения сущности контракта по заданию
     *
     * @param task - сущность задания
     * @return - Объект Optional с сущностью контракта или null, если контракт с данным заданием не существует
     */
    Optional<Contract> findContractByTask(Task task);

    /**
     * Метод получения всех контрактов с переданным статусом
     *
     * @param contractStatus - статус контракта
     * @return - коллекция контрактов, с переданным статусом
     */
    Collection<Contract> findContractsByContractStatus(ContractStatus contractStatus);

}
