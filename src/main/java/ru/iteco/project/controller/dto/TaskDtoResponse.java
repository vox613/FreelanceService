package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности Task, содержащего данные для отображения на фронте
 */
public class TaskDtoResponse extends TaskBaseDto {

    /*** Статус задания */
    private String taskStatus;

    /*** Решение задания */
    private String taskDecision;

    /*** Заказчик */
    private UserBaseDto customer;

    /*** Исполнитель */
    private UserBaseDto executor;

    /*** Дата и время создания задания */
    private String createdAt;

    /*** Дата и время последнего обновления задания */
    private String updatedAt;


    public TaskDtoResponse() {
    }

    public UserBaseDto getCustomer() {
        return customer;
    }

    public void setCustomer(UserBaseDto customer) {
        this.customer = customer;
    }

    public UserBaseDto getExecutor() {
        return executor;
    }

    public void setExecutor(UserBaseDto executor) {
        this.executor = executor;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
