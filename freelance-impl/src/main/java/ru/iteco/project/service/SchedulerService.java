package ru.iteco.project.service;

/**
 * Интерфейс описывает общий функционал Service слоя для отложенных заданий и заданий по CRON расписанию
 */
public interface SchedulerService {

    /**
     * Метод удаляет задания которые не приняты в работу (taskStatus = REGISTERED) и срок выполнения которых просрочен
     * на заданное в {task.scheduler.taskCompletionDate.expiredDays} количество дней
     */
    void taskDeletingOverdueTasks();

}
