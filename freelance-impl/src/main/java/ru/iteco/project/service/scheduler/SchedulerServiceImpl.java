package ru.iteco.project.service.scheduler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.enumaration.AuditCode;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.repository.BookKeepingReportRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.TaskStatusRepository;
import ru.iteco.project.resource.dto.JmsSendStatus;

import java.time.LocalDateTime;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.REGISTERED;

/**
 * Класс реализует функционал сервисного слоя для работы с отложенными заданиями и заданиями по CRON расписанию
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {
    private static final Logger log = LogManager.getLogger(SchedulerServiceImpl.class.getName());

    @Value("${errors.task.status.invalid}")
    private String invalidTaskStatusMessage;

    @Value("${task.scheduler.taskCompletionDate.expiredDays}")
    private Integer expiredDays;


    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию лога отправки бухгалтерских отчетов */
    private final BookKeepingReportRepository bookKeepingReportRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;


    public SchedulerServiceImpl(TaskRepository taskRepository, BookKeepingReportRepository bookKeepingReportRepository,
                                TaskStatusRepository taskStatusRepository) {
        this.taskRepository = taskRepository;
        this.bookKeepingReportRepository = bookKeepingReportRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void taskDeletingOverdueTasks() {
        TaskStatus taskStatus = taskStatusRepository.findTaskStatusByValue(REGISTERED.name())
                .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage));
        LocalDateTime redLine = LocalDateTime.now().minusDays(expiredDays);
        taskRepository.scheduledDeletingTasks(taskStatus, redLine);
        log.info("Removed tasks with due date {} and earlier", redLine);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void confirmedReportsDeletingTask() {
        log.info("Start removing bookkeeping logs with reportStatus=CONFIRMED");
        bookKeepingReportRepository.deleteAllByReportStatusIs(JmsSendStatus.CONFIRMED);
        log.info("End removing bookkeeping logs with reportStatus=CONFIRMED");
    }
}
