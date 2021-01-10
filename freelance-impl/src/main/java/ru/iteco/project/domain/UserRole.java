package ru.iteco.project.domain;

import ru.iteco.project.exception.InvalidUserRoleException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Модель данных пердставляющая роли пользователей
 */
@Entity
@Table(name = "user_roles")
public class UserRole extends CreateAtIdentified implements Identified<UUID> {

    /*** Уникальный id роли пользователя */
    @Id
    @Column
    private UUID id;

    /*** Наименование роли пользователя */
    @Column(nullable = false, unique = true)
    private String value;


    public UserRole() {
    }

    public UserRole(UUID id, String value) {
        this.id = id;
        this.value = value;
    }


    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    /**
     * Перечисление основных ролей пользователей
     */
    public enum UserRoleEnum {
        ADMIN,
        CUSTOMER,
        EXECUTOR;


        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputUserRole - текстовое представление роли пользователя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputUserRole) {
            for (UserRoleEnum userRole : values()) {
                if (userRole.name().equals(inputUserRole)) {
                    return true;
                }
            }
            return false;
        }


        public UserRoleEnum userRoleEnumFromValue(String inputUserRole) {
            if (isCorrectValue(inputUserRole)) {
                return valueOf(inputUserRole);
            }
            throw new InvalidUserRoleException();
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param userRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param user         - сущность пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserRole(UserRoleEnum userRoleEnum, User user) {
            UserRole role = user.getRole();
            return (role != null) && isEqualsUserRole(userRoleEnum, role.getValue());
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param userRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param userRole     - строковое представление роли пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsUserRole(UserRoleEnum userRoleEnum, String userRole) {
            if ((userRoleEnum != null) && isCorrectValue(userRole)) {
                return userRoleEnum == UserRoleEnum.valueOf(userRole);
            }
            return false;
        }
    }

}
