package ru.iteco.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Перечисление возможных статусов договора
 */
@Entity
@Table(name = "contract_statuses")
public class ContractStatus extends CreateAtIdentified implements Identified<UUID> {

    /*** Уникальный id статуса контракта */
    @Id
    @Column
    private UUID id;

    /*** Наименование статуса контракта */
    @Column(nullable = false, unique = true)
    private String value;

    /*** Описание статуса контракта */
    @Column(nullable = false)
    private String description;


    public ContractStatus() {
    }

    public ContractStatus(UUID id, String value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }

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


    public enum ContractStatusEnum {
        PAID("Договор оплачен"),
        TERMINATED("Договор расторгнут"),
        DONE("Договор исполнен");

        private final String description;

        ContractStatusEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }


        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputContractStatus - текстовое представление статуса контракта
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputContractStatus) {
            for (ContractStatusEnum statusEnum : values()) {
                if (statusEnum.name().equals(inputContractStatus)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентен ли статус контракта переданному значению статуса контракта
         *
         * @param contractStatusEnum - Элемент перечисления доступных статусов контракта
         * @param contract           - сущность контракта
         * @return true - статус контракта эквивалентен переданному значению,
         * false - статус контракта не эквивалентен переданному значению
         */
        public static boolean isEqualsContractStatus(ContractStatusEnum contractStatusEnum, Contract contract) {
            ContractStatus contractStatus = contract.getContractStatus();
            return (contractStatus != null) && isEqualsContractStatus(contractStatusEnum, contractStatus.getValue());
        }


        /**
         * Метод проверяет эквивалентен ли статус задания переданному значению статуса задания
         *
         * @param contractStatusEnum - Элемент перечисления доступных статусов задания
         * @param contractStatus     - строковое представление статуса задания
         * @return true - статус задания эквивалентен переданному значению,
         * false - статус задания не эквивалентен переданному значению
         */
        public static boolean isEqualsContractStatus(ContractStatusEnum contractStatusEnum, String contractStatus) {
            if ((contractStatusEnum != null) && isCorrectValue(contractStatus)) {
                return contractStatusEnum == ContractStatusEnum.valueOf(contractStatus);
            }
            return false;
        }
    }
}
