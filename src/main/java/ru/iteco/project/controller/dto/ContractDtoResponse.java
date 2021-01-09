package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности Contract, содержащего данные для отображения на фронте
 */
public class ContractDtoResponse extends ContractBaseDto {

    /*** Статус договора */
    private String contractStatus;

    /*** Заказчик */
    private UserBaseDto customer;

    /*** Исполнитель */
    private UserBaseDto executor;

    /*** Задание */
    private TaskBaseDto task;

    /*** Дата и время создания статуса контракта */
    private String createdAt;

    /*** Дата и время последнего обновления статуса контракта */
    private String updatedAt;


    public ContractDtoResponse() {
    }


    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
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

    public TaskBaseDto getTask() {
        return task;
    }

    public void setTask(TaskBaseDto task) {
        this.task = task;
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
