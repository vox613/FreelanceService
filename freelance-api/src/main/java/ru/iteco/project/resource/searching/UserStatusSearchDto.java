package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Модель запроса для поиска статусов пользователей")
public class UserStatusSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование статуса пользователя", allowEmptyValue = true)
    private SearchUnit value;

    @ApiModelProperty(value = "Описание статуса пользователя", allowEmptyValue = true)
    private SearchUnit description;


    public UserStatusSearchDto() {
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
    public UserStatusSearchDto searchData() {
        return this;
    }
}
