package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.AbstractSearchDto;
import ru.iteco.project.resource.SearchUnit;

@ApiModel(description = "Модель запроса для поиска статусов пользователей")
public class ClientStatusSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование статуса пользователя", allowEmptyValue = true)
    private SearchUnit value;

    @ApiModelProperty(value = "Описание статуса пользователя", allowEmptyValue = true)
    private SearchUnit description;


    public ClientStatusSearchDto() {
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
    public ClientStatusSearchDto searchData() {
        return this;
    }
}
