package ru.iteco.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.domain.UserStatus;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс JPA репозитория для предоставления методов взаимодействия с данными сущности User
 */
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    boolean existsByEmailOrLogin(String email, String login);

    /**
     * Метод получения всех пользователей с переданным статусом
     *
     * @param userStatus - статус пользователя
     * @return - коллекция пользователей, с переданным статусом
     */
    Collection<User> findAllByUserStatus(UserStatus userStatus);

    /**
     * Метод получения всех пользователей с переданной ролью
     *
     * @param userRole - роль пользователя
     * @return - коллекция пользователей, с переданной ролью
     */
    Collection<User> findAllByRole(UserRole userRole);

}
