package ru.iteco.project.resource.searching;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.AbstractSearchDto;
import ru.iteco.project.resource.SearchUnit;


@ApiModel(description = "Модель запроса для поиска ролей пользователей")
public class ClientRoleSearchDto extends AbstractSearchDto {

    @ApiModelProperty(value = "Наименование роли пользователя", allowEmptyValue = true)
    private SearchUnit value;


    public ClientRoleSearchDto() {
    }

    public SearchUnit getValue() {
        return value;
    }

    public void setValue(SearchUnit value) {
        this.value = value;
    }


    @Override
    public ClientRoleSearchDto searchData() {
        return this;
    }
}
