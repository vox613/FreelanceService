package ru.iteco.project.controller.searching;

/**
 * Класс - dto для поиска контрактов с переданными ограничениями по полям
 */
public class ContractSearchDto extends AbstractSearchDto {

    /*** Статус контракта */
    private SearchUnit contractStatus;

    /*** Дата и время создания контракта */
    private SearchUnit createdAt;

    /*** Дата и время последнего обновления контракта */
    private SearchUnit updatedAt;


    public ContractSearchDto() {
    }


    public SearchUnit getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(SearchUnit contractStatus) {
        this.contractStatus = contractStatus;
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
    public ContractSearchDto searchData() {
        return this;
    }
}
