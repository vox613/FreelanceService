package ru.iteco.project.service.jms;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.domain.BookKeepingReport;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.exception.EmptyAckMessageException;
import ru.iteco.project.exception.EmptyReportException;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.BookKeepingReportRepository;
import ru.iteco.project.resource.dto.BookKeepingReportDto;
import ru.iteco.project.resource.dto.ContractReportAckMessage;
import ru.iteco.project.resource.dto.JmsSendStatus;

import java.time.LocalDateTime;

import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.DONE;
import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.isEqualsContractStatus;

/**
 * Класс сервисного слоя для взаимодействия с BookKeeping
 */
@Service
@PropertySource(value = {"classpath:errors.properties", "classpath:application.yml"})
public class BookKeepingServiceImpl implements BookKeepingService {
    private static final Logger log = LogManager.getLogger(BookKeepingServiceImpl.class.getName());

    @Value("${iteco.jms.queue.bookKeepingQueue}")
    private String queueNameUser;

    @Value("${errors.bookkeeping.document.invalid}")
    private String contractInvalidMessage;

    @Value("${errors.bookkeeping.ack.empty}")
    private String emptyAckMessage;

    /*** Объект класса для отправки сообщений в JMS*/
    private final JmsMessageSender jmsMessageSender;

    /*** Объект маппера dto <-> сущность договога */
    private final MapperFacade mapperFacade;

    /*** Объект доступа к репозиторию контрактов */
    private final BookKeepingReportRepository bookKeepingReportRepository;


    public BookKeepingServiceImpl(JmsMessageSender jmsMessageSender, MapperFacade mapperFacade, BookKeepingReportRepository bookKeepingReportRepository) {
        this.jmsMessageSender = jmsMessageSender;
        this.mapperFacade = mapperFacade;
        this.bookKeepingReportRepository = bookKeepingReportRepository;
    }


    @Override
    public void sendReportToBookKeeping(Contract contract) {
        if ((contract == null) || (contract.getId() == null)
                || !isEqualsContractStatus(DONE, contract.getContractStatus().getValue())) {
            log.error(contractInvalidMessage);
            throw new EmptyReportException(contractInvalidMessage);
        }

        BookKeepingReport bookKeepingReport = new BookKeepingReport(contract.getId(), LocalDateTime.now());
        BookKeepingReportDto bookKeepingReportDto = mapperFacade.map(contract, BookKeepingReportDto.class);
        bookKeepingReport.getReport().put(bookKeepingReportDto.getClass().getSimpleName(), bookKeepingReportDto);

        boolean isSuccessSend = jmsMessageSender.sendBookKeepingReport(queueNameUser, bookKeepingReportDto);
        bookKeepingReport.setReportStatus(isSuccessSend ? JmsSendStatus.SENDED : JmsSendStatus.SEND_ERROR);
        bookKeepingReportRepository.save(bookKeepingReport);
    }

    @Override
    public void proceedAckMessage(ContractReportAckMessage message) {
        if (message == null) {
            throw new EmptyAckMessageException(emptyAckMessage);
        }
        BookKeepingReport bookKeepingReport = bookKeepingReportRepository.findById(message.getContractReportId())
                .orElseThrow(EntityRecordNotFoundException::new);

        bookKeepingReport.setReportStatus(message.getAckStatus() ? JmsSendStatus.CONFIRMED : JmsSendStatus.SEND_ERROR);
        bookKeepingReportRepository.save(bookKeepingReport);
    }
}
