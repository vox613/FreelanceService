package ru.iteco.project.schedule;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iteco.project.service.scheduler.SchedulerService;

import java.time.Clock;
import java.time.Instant;

/**
 * Инициализация и вызов отложенного Cron задания
 */
@Component
public class CronSchedule {
    private static final Logger log = LogManager.getLogger(CronSchedule.class.getName());

    @Value("${scheduling.task.cron.enabled}")
    boolean taskCronIsEnabled;

    @Value("${scheduling.bookkeeping.cron.enabled}")
    boolean bookkeepingCronIsEnabled;

    /*** Объект Clock для логирования времени выполнения*/
    private final Clock clock;

    /*** Объект сервисного слоя отложенных заданий */
    private final SchedulerService schedulerService;

    public CronSchedule(Clock clock, SchedulerService schedulerService) {
        this.clock = clock;
        this.schedulerService = schedulerService;
    }

    @Scheduled(cron = "${scheduling.task.cron.expression}")
    public void scheduleTask() {
        if (taskCronIsEnabled) {
            Instant now = clock.instant();
            log.info("cron scheduleTask start:\t current time: {}", now);
            schedulerService.taskDeletingOverdueTasks();
            log.info("cron scheduleTask end:\t current time: {}", now);
        }
    }

    @Scheduled(cron = "${scheduling.bookkeeping.cron.expression}")
    public void scheduleBookkeeping() {
        if (bookkeepingCronIsEnabled) {
            Instant now = clock.instant();
            log.info("cron scheduleBookkeeping start:\t current time: {}", now);
            schedulerService.confirmedReportsDeletingTask();
            log.info("cron scheduleBookkeeping end:\t current time: {}", now);
        }
    }
}
