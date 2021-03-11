package ru.iteco.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Модель данных представляющая статусы пользователей
 */
@Entity
@Table(schema = "freelance", name = "client_statuses")
public class ClientStatus extends CreateAtIdentified implements Identified<UUID> {

    /*** Уникальный id роли пользователя */
    @Id
    @Column
    private UUID id;

    /*** Наименование роли пользователя */
    @Column(nullable = false, unique = true)
    private String value;

    /*** Описание роли пользователя */
    @Column(nullable = false)
    private String description;


    public ClientStatus() {
    }

    public ClientStatus(UUID id, String value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Перечисление возможных статусов в которых может находитья пользователь
     */
    public enum ClientStatusEnum {
        NOT_EXIST("Пользователя не существует"),
        CREATED("Создан"),
        BLOCKED("Заблокирован"),
        ACTIVE("Активен");

        private final String description;

        ClientStatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputClientStatus - текстовое представление статуса пользователя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputClientStatus) {
            for (ClientStatusEnum clientStatus : values()) {
                if (clientStatus.name().equals(inputClientStatus)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентен ли статус пользователя переданному значению статуса пользователя
         *
         * @param clientStatusEnum - Элемент перечисления доступных статусов пользователя
         * @param client           - сущность пользователя
         * @return true - статус пользоваателя эквивалентен переданному хначению,
         * false - статус пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsClientStatus(ClientStatusEnum clientStatusEnum, Client client) {
            ClientStatus clientStatus = client.getClientStatus();
            return (clientStatus != null) && isEqualsClientStatus(clientStatusEnum, clientStatus.getValue());
        }


        /**
         * Метод проверяет эквивалентен ли статус пользователя переданному значению статуса пользователя
         *
         * @param clientStatusEnum - Элемент перечисления доступных статусов пользователя
         * @param clientStatus     - строковое представление статуса пользователя
         * @return true - статус пользоваателя эквивалентен переданному хначению,
         * false - статус пользователя не эквивалентен переданному значению
         */
        public static boolean isEqualsClientStatus(ClientStatusEnum clientStatusEnum, String clientStatus) {
            if ((clientStatusEnum != null) && isCorrectValue(clientStatus)) {
                return clientStatusEnum == ClientStatusEnum.valueOf(clientStatus);
            }
            return false;
        }
    }

}
