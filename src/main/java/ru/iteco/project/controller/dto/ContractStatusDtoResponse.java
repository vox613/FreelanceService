package ru.iteco.project.controller.dto;

/**
 * Класс для формирования dto объекта сущности ContractStatus, содержащего данные для отображения на фронте
 */
public class ContractStatusDtoResponse extends ContractStatusBaseDto {

    /*** Дата и время создания статуса контракта */
    private String createdAt;

    /*** Дата и время последнего обновления статуса контракта */
    private String updatedAt;


    public ContractStatusDtoResponse() {
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
