package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Модель запроса для поиска ролей пользователей")
public class UserRoleSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование роли пользователя", allowEmptyValue = true)
    private SearchUnit value;


    public UserRoleSearchDto() {
    }

    public SearchUnit getValue() {
        return value;
    }

    public void setValue(SearchUnit value) {
        this.value = value;
    }


    @Override
    public UserRoleSearchDto searchData() {
        return this;
    }
}
