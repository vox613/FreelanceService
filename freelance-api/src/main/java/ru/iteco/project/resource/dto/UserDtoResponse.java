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

    @ApiModelProperty(value = "Список id заданий пользователя", example = "[bf51c162-95f3-4e69-ab6d-7ff214430ba6]",
            required = true,
            allowEmptyValue = true)
    private final List<UUID> tasksIdList = new ArrayList<>();


    public UserDtoResponse() {
    }

    public List<UUID> getTasksIdList() {
        return tasksIdList;
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
