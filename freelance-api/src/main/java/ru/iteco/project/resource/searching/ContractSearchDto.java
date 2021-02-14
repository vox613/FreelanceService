package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.AbstractSearchDto;
import ru.iteco.project.resource.SearchUnit;

@ApiModel(description = "Модель запроса для поиска контрактов")
public class ContractSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Статус контракта", allowEmptyValue = true)
    private SearchUnit contractStatus;


    public ContractSearchDto() {
    }


    public SearchUnit getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(SearchUnit contractStatus) {
        this.contractStatus = contractStatus;
    }

    @Override
    public ContractSearchDto searchData() {
        return this;
    }
}
