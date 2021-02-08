package ru.iteco.project.schedule;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iteco.project.service.SchedulerService;

import java.time.Clock;
import java.time.Instant;

/**
 * Инициализация и вызов отложенного Cron задания
 */
@Component
public class CronSchedule {
    private static final Logger log = LogManager.getLogger(CronSchedule.class.getName());

    @Value("${scheduling.askResending.cron.enabled}")
    boolean askResendingCronIsEnabled;

    /*** Объект Clock для логирования времени выполнения*/
    private final Clock clock;

    /*** Объект сервисного слоя отложенных заданий */
    private final SchedulerService schedulerService;

    public CronSchedule(Clock clock, SchedulerService schedulerService) {
        this.clock = clock;
        this.schedulerService = schedulerService;
    }

    @Scheduled(cron = "${scheduling.askResending.cron.expression}")
    public void schedule() {
        if (askResendingCronIsEnabled) {
            Instant now = clock.instant();
            log.info("cron start:\t current time: {}", now);
            schedulerService.resendingAckMessages();
            log.info("cron end:\t current time: {}", now);
        }
    }
}
