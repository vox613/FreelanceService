package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.TaskStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности TaskStatus
 */
public interface TaskStatusRepository extends JpaRepository<TaskStatus, UUID>, JpaSpecificationExecutor<TaskStatus> {

    /**
     * Метод получения сущности статуса задания по его строковому представлению
     *
     * @param value - строковое представление статуса задания
     * @return - Объект Optional с сущностью статуса задания или с null, если статус задания с данным значением не существует
     */
    Optional<TaskStatus> findTaskStatusByValue(String value);

}
