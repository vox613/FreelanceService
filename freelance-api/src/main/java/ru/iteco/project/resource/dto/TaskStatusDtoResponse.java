package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Данные модели статуса задания для ответа")
public class TaskStatusDtoResponse extends TaskStatusBaseDto {

    @ApiModelProperty(value = "Дата и время создания статуса задания", example = "2020-12-28 03:47:32",
            required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления статуса задания",
            example = "2020-12-28 03:47:32",
            required = true)
    private String updatedAt;

    public TaskStatusDtoResponse() {
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
