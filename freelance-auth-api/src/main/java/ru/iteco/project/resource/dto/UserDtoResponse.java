package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@ApiModel(description = "Данные модели пользователя для ответа")
public class UserDtoResponse extends UserBaseDto {

    @ApiModelProperty(value = "Дата и время создания пользователя", example = "2020-12-28 03:47:32", required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления пользователя", example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;


    public UserDtoResponse() {
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
