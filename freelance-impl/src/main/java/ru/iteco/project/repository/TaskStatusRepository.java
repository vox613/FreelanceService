package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.iteco.project.domain.TaskStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности TaskStatus
 */
@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, UUID>, JpaSpecificationExecutor<TaskStatus> {

    /**
     * Метод получения сущности статуса задания по его строковому представлению
     *
     * @param value - строковое представление статуса задания
     * @return - Объект Optional с сущностью статуса задания или с null, если статус задания с данным значением не существует
     */
    Optional<TaskStatus> findTaskStatusByValue(String value);

    /**
     * Метод проверяет существование статуса задания с переданным значением поля value
     *
     * @param value - наименование статуса задания
     * @return true - статус задания с переданным значением существует,
     * false - статус задания с переданным значением статуса не существует
     */
    Boolean existsTaskStatusByValue(String value);
}
