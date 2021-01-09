package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска статусов контрактов с переданными ограничениями по полям
 */
public class ContractStatusSearchDto extends AbstractSearchDto {

    /*** Наименование статуса контракта */
    private SearchUnit value;

    /*** Описание статуса контракта */
    private SearchUnit description;

    /*** Дата и время создания статуса контракта */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления статуса контракта */
    private SearchUnit updatedAt;


    public ContractStatusSearchDto() {
    }

    public SearchUnit getValue() {
        return value;
    }

    public void setValue(SearchUnit value) {
        this.value = value;
    }

    public SearchUnit getDescription() {
        return description;
    }

    public void setDescription(SearchUnit description) {
        this.description = description;
    }

    public SearchUnit getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(SearchUnit createdAt) {
        this.createdAt = createdAt;
    }

    public SearchUnit getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(SearchUnit updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public ContractStatusSearchDto searchData() {
        return this;
    }
}
