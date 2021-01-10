package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Данные модели роли пользователя для ответа")
public class UserRoleDtoResponse extends UserRoleBaseDto {

    @ApiModelProperty(value = "Дата и время создания роли пользователя", example = "2020-12-28 03:47:32",
            required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления роли пользователя", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public UserRoleDtoResponse() {
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
