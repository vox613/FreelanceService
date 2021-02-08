package ru.iteco.project.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.ContractReport;
import ru.iteco.project.repository.ContractReportsRepository;
import ru.iteco.project.resource.dto.BookKeepingReportDto;
import ru.iteco.project.resource.dto.ContractReportAckMessage;
import ru.iteco.project.resource.dto.JmsSendStatus;
import ru.iteco.project.service.jms.JmsMessageSender;

import java.time.LocalDateTime;

import static ru.iteco.project.logger.utils.LoggerUtils.*;

/**
 * Сервисный слой для взаимодействия с объектами ContractReport
 */
@Service
public class ContractReportServiceImpl implements ContractReportService {
    private static final Logger log = LogManager.getLogger(ContractReportServiceImpl.class.getName());

    @Value("${iteco.jms.queue.bookKeepingAckQueue}")
    private String bookKeepingAckQueue;

    /*** Объект класса для отправки сообщений в JMS*/
    private final JmsMessageSender jmsMessageSender;

    /*** Репозиторий для взаимодействия  сущностями ContractReport*/
    private final ContractReportsRepository contractReportsRepository;

    /*** Объект сервисного слоя для взаимодействия  сущностями ContractReport*/
    private ContractReportService contractReportService;


    @Lazy
    @Autowired
    public void setContractReportService(ContractReportService contractReportService) {
        this.contractReportService = contractReportService;
    }

    public ContractReportServiceImpl(JmsMessageSender jmsMessageSender, ContractReportsRepository contractReportsRepository) {
        this.jmsMessageSender = jmsMessageSender;
        this.contractReportsRepository = contractReportsRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void proceedQueueEvent(BookKeepingReportDto bookKeepingReportDto) {
        beforeCall(Level.DEBUG,"proceedQueueEvent", bookKeepingReportDto);
        if ((bookKeepingReportDto != null) && (bookKeepingReportDto.getId() != null)) {
            ContractReport contractReport = new ContractReport(bookKeepingReportDto.getId(), LocalDateTime.now());
            contractReport.getReport().put(bookKeepingReportDto.getClass().getSimpleName(), bookKeepingReportDto);
            contractReport.setAckStatus(JmsSendStatus.SENDED);
            contractReportsRepository.save(contractReport);

            contractReportService.renewAckStatus(contractReport);
            afterCall(Level.DEBUG, "proceedQueueEvent", contractReport);
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void renewAckStatus(ContractReport contractReport) {
        beforeCall(Level.DEBUG, "renewAckStatus", contractReport);
        ContractReportAckMessage contractReportAckMessage = new ContractReportAckMessage(contractReport.getId(), true);
        boolean isSuccessSend = jmsMessageSender.sendAckMessage(bookKeepingAckQueue, contractReportAckMessage);
        contractReport.setAckStatus(isSuccessSend ? JmsSendStatus.CONFIRMED : JmsSendStatus.SEND_ERROR);
        contractReportsRepository.save(contractReport);
        afterCall(Level.DEBUG, "renewAckStatus", contractReport);
    }

}
