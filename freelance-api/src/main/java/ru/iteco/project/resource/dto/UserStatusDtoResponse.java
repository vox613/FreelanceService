package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(description = "Данные модели статуса пользователя для ответа")
public class UserStatusDtoResponse extends UserStatusBaseDto {

    @ApiModelProperty(value = "Дата и время создания статуса пользователя", example = "2020-12-28 03:47:32",
            required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления статуса пользователя", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public UserStatusDtoResponse() {
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
