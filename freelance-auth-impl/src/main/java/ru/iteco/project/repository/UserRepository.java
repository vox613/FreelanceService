package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности User
 */
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    /**
     * Метод проверяет существует ли пользователь с переданними логином или emil
     *
     * @param email    - электронная почта
     * @param username - имя пользователя
     * @return - true -пользователь с переданными данными существует,
     * false - пользователя с переданными данными не существует
     */
    boolean existsByEmailOrUsername(String email, String username);


    /**
     * Метод проверяет существует ли пользователь с переданной электронной почтой
     *
     * @param email - электронная почта
     * @return - true - пользователь с переданным email существует,
     * false - пользователя с переданным email не существует
     */
    boolean existsByEmail(String email);


    /**
     * Метод поиска сущности пользователя по его username
     * @param username - логин/уникальное имя пользователя
     * @return - Optional c сущностью пользователя, если пользователь с данным username сущетвует, иначе Optional c null
     */
    Optional<User> findByUsername(String username);

}
