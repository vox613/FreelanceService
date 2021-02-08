package ru.iteco.project.service;

/**
 * Интерфейс описывает общий функционал Service слоя для отложенных заданий и заданий по CRON расписанию
 */
public interface SchedulerService {

    /**
     * Сигнатура метода для осуществления повторной отправки сообщения-подтверждения о получении отчета
     */
    void resendingAckMessages();

}
