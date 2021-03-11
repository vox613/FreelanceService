package ru.iteco.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Модель данных пердставляющая статусы задания
 */
@Entity
@Table(schema = "freelance", name = "task_statuses")
public class TaskStatus extends CreateAtIdentified implements Identified<UUID> {

    /*** Уникальный id статуса задания */
    @Id
    @Column
    private UUID id;

    /*** Наименование статуса задания */
    @Column(nullable = false, unique = true)
    private String value;

    /*** Описание статуса задания */
    @Column(nullable = false)
    private String description;


    public TaskStatus() {
    }

    public TaskStatus(UUID id, String value, String description) {
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
     * Перечисление возможных статусов задания
     */
    public enum TaskStatusEnum {
        REGISTERED("Задание зарегистрировано", true),
        IN_PROGRESS("Задание на выполнении", false),
        ON_CHECK("Задание на проверке", false),
        ON_FIX("Задание на исправлении", false),
        DONE("Задание выполнено", true),
        CANCELED("Задание отменено", true);

        private final String description;
        private final boolean isTerminated;

        TaskStatusEnum(String description, boolean isTerminated) {
            this.description = description;
            this.isTerminated = isTerminated;
        }

        public String getDescription() {
            return description;
        }

        public boolean isTerminated() {
            return isTerminated;
        }

        /**
         * Метод проверяет является ли входная строка текстовым представлением одного из элементов перечисления
         *
         * @param inputTaskStatus - текстовое представление статуса заданя
         * @return true - в перечислении присутствует аргумент с данным именем,
         * false - в перечислении отсутствует аргумент с данным именем
         */
        public static boolean isCorrectValue(String inputTaskStatus) {
            for (TaskStatusEnum statusEnum : values()) {
                if (statusEnum.name().equals(inputTaskStatus)) {
                    return true;
                }
            }
            return false;
        }


        /**
         * Метод проверяет эквивалентен ли статус задания переданному значению статуса задания
         *
         * @param taskStatusEnum - Элемент перечисления доступных статусов задания
         * @param task           - сущность задания
         * @return true - статус задания эквивалентен переданному значению,
         * false - статус задания не эквивалентен переданному значению
         */
        public static boolean isEqualsTaskStatus(TaskStatusEnum taskStatusEnum, Task task) {
            TaskStatus taskStatus = task.getTaskStatus();
            return (taskStatus != null) && isEqualsTaskStatus(taskStatusEnum, taskStatus.getValue());
        }


        /**
         * Метод проверяет эквивалентен ли статус задания переданному значению статуса задания
         *
         * @param taskStatusEnum - Элемент перечисления доступных статусов задания
         * @param taskStatus     - строковое представление статуса задания
         * @return true - статус задания эквивалентен переданному значению,
         * false - статус задания не эквивалентен переданному значению
         */
        public static boolean isEqualsTaskStatus(TaskStatusEnum taskStatusEnum, String taskStatus) {
            if ((taskStatusEnum != null) && isCorrectValue(taskStatus)) {
                return taskStatusEnum == TaskStatusEnum.valueOf(taskStatus);
            }
            return false;
        }
    }

}
