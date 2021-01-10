package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Модель запроса для поиска статусов контрактов")
public class ContractStatusSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование статуса контракта", allowEmptyValue = true)
    private SearchUnit value;

    @ApiModelProperty(value = "Описание статуса контракта", allowEmptyValue = true)
    private SearchUnit description;


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

    @Override
    public ContractStatusSearchDto searchData() {
        return this;
    }
}
