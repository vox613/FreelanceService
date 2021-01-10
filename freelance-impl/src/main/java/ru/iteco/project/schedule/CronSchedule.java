package ru.iteco.project.schedule;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iteco.project.service.SchedulerService;

import java.time.Clock;
import java.time.Instant;

/**
 * Инициализация и вызов отложенного Cron задания
 */
@Component
@ConditionalOnProperty(prefix = "scheduling.cron", name = {"enabled"}, matchIfMissing = false)
public class CronSchedule {
    private static final Logger log = LogManager.getLogger(CronSchedule.class.getName());

    /*** Объект Clock для логирования времени выполнения*/
    private final Clock clock;

    /*** Объект сервисного слоя отложенных заданий */
    private final SchedulerService schedulerService;

    public CronSchedule(Clock clock, SchedulerService schedulerService) {
        this.clock = clock;
        this.schedulerService = schedulerService;
    }

    @Scheduled(cron = "${scheduling.cron.expression}")
    public void schedule() {
        Instant now = clock.instant();
        log.info("cron start:\t current time: {}", now);
        schedulerService.taskDeletingOverdueTasks();
        log.info("cron end:\t current time: {}", now);
    }
}
