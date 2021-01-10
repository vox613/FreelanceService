package ru.iteco.project.resource.searching;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ru.iteco.project.resource.dto.DtoInterface;


@ApiModel(description = "Базовая модель запроса для поиска по объекатам")
public class AbstractSearchDto implements SearchDto {

    @ApiModelProperty(value = "Тип операции объединяющей предикаты поиска", example = "AND",
            allowableValues = "AND, OR")
    private String joinOperation;

    @ApiModelProperty(value = "Дата и время создания записи", allowEmptyValue = true)
    private SearchUnit createdAt;


    @ApiModelProperty(value = "Дата и время последнего обновления записи", allowEmptyValue = true)
    private SearchUnit updatedAt;


    public AbstractSearchDto() {
    }


    public String getJoinOperation() {
        return joinOperation;
    }

    public void setJoinOperation(String joinOperation) {
        this.joinOperation = joinOperation;
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
    public DtoInterface searchData() {
        return this;
    }
}
