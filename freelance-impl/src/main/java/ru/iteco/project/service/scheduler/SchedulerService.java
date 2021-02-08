package ru.iteco.project.service.scheduler;

/**
 * Интерфейс описывает общий функционал Service слоя для отложенных заданий и заданий по CRON расписанию
 */
public interface SchedulerService {

    /**
     * Метод удаляет задания которые не приняты в работу (taskStatus = REGISTERED) и срок выполнения которых просрочен
     * на заданное в {task.scheduler.taskCompletionDate.expiredDays} количество дней
     */
    void taskDeletingOverdueTasks();


    /**
     * Метод удаляет из таблицы лога отправки отчетов записи со статусом (reportStatus = CONFIRMED) c периодичностью заданной в
     * {scheduling.bookkeeping.cron.expression}
     */
    void confirmedReportsDeletingTask();
}
