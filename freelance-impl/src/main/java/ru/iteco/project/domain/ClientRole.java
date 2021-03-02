package ru.iteco.project.domain;

import ru.iteco.project.exception.InvalidClientRoleException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Модель данных пердставляющая роли пользователей
 */
@Entity
@Table(schema = "freelance", name = "Client_roles")
public class ClientRole extends CreateAtIdentified implements Identified<UUID> {

    /*** Уникальный id роли пользователя */
    @Id
    @Column
    private UUID id;

    /*** Наименование роли пользователя */
    @Column(nullable = false, unique = true)
    private String value;


    public ClientRole() {
    }

    public ClientRole(UUID id, String value) {
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
    public enum ClientRoleEnum {
        ADMIN,
        CUSTOMER,
        EXECUTOR;


        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputClientRole - текстовое представление роли пользователя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputClientRole) {
            for (ClientRoleEnum clientRole : values()) {
                if (clientRole.name().equals(inputClientRole)) {
                    return true;
                }
            }
            return false;
        }


        public ClientRoleEnum clientRoleEnumFromValue(String inputClientRole) {
            if (isCorrectValue(inputClientRole)) {
                return valueOf(inputClientRole);
            }
            throw new InvalidClientRoleException();
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param clientRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param client         - сущность пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsClientRole(ClientRoleEnum clientRoleEnum, Client client) {
            ClientRole role = client.getClientRole();
            return (role != null) && isEqualsClientRole(clientRoleEnum, role.getValue());
        }


        /**
         * Метод проверяет эквивалентна ли роль пользователя переданному значению роли пользователя
         *
         * @param clientRoleEnum - Элемент перечисления доступных ролей пользователя
         * @param clientRole     - строковое представление роли пользователя
         * @return true - роль пользоваателя эквивалентен переданному значению,
         * false - роль пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsClientRole(ClientRoleEnum clientRoleEnum, String clientRole) {
            if ((clientRoleEnum != null) && isCorrectValue(clientRole)) {
                return clientRoleEnum == ClientRoleEnum.valueOf(clientRole);
            }
            return false;
        }
    }

}
