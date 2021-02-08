package ru.iteco.project.service;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.ContractReport;
import ru.iteco.project.repository.ContractReportsRepository;
import ru.iteco.project.resource.dto.JmsSendStatus;

import java.util.Collection;

import static ru.iteco.project.logger.utils.LoggerUtils.CALL_PATTERN;
import static ru.iteco.project.logger.utils.LoggerUtils.logMethodCall;


/**
 * Класс реализует функционал сервисного слоя для работы с отложенными заданиями и заданиями по CRON расписанию
 */
@Service
public class SchedulerServiceImpl implements SchedulerService {

    /*** Репозиторий для взаимодействия  сущностями ContractReport*/
    private final ContractReportsRepository contractReportsRepository;

    /*** Объект сервисного слоя для взаимодействия  сущностями ContractReport*/
    private final ContractReportService contractReportService;

    public SchedulerServiceImpl(ContractReportsRepository contractReportsRepository, ContractReportService contractReportService) {
        this.contractReportsRepository = contractReportsRepository;
        this.contractReportService = contractReportService;
    }

    @Override
    public void resendingAckMessages() {
        Collection<ContractReport> allByAckStatusIsNot = contractReportsRepository
                .findAllByAckStatusIsNot(JmsSendStatus.CONFIRMED);
        if (allByAckStatusIsNot.size() != 0) {
            for (ContractReport contractReport : allByAckStatusIsNot) {
                contractReportService.renewAckStatus(contractReport);
                logMethodCall(Level.DEBUG, CALL_PATTERN,"resendingAckMessages", contractReport.getId());
            }
        }
    }
}
