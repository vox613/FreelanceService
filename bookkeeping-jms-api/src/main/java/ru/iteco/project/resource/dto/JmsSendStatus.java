package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Возможные статусы результата отправки отчета/подтверждения приема сообщения в очередь")
public enum JmsSendStatus {
    SENDED("Документ отправлен"),
    SEND_ERROR("Ошибка отправки документа"),
    CONFIRMED("Получение документа подтверждено");

    /*** Текстовое описание статуса отправки сообщения в очередь*/
    private final String description;

    JmsSendStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
