package ru.iteco.project.service.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.iteco.project.resource.dto.BookKeepingReportDto;
import ru.iteco.project.service.ContractReportServiceImpl;

/**
 * Слушатель JMS событий
 */
@Component
public class JmsMessageListener {
    private static final Logger log = LogManager.getLogger(JmsMessageListener.class);

    /*** Объект сервисного слоя для ContractReport*/
    private final ContractReportServiceImpl contractJmsService;

    public JmsMessageListener(ContractReportServiceImpl contractJmsService) {
        this.contractJmsService = contractJmsService;
    }

    /**
     * Слушатель JMS очереди iteco.jms.queue.bookKeepingQueue
     *
     * @param message - входящее сообщение из очереди, объект BookKeepingReportDto
     */
    @JmsListener(destination = "${iteco.jms.queue.bookKeepingQueue}")
    public void receiveMessage(BookKeepingReportDto message) {
        log.info("Received message {}", message);
        contractJmsService.proceedQueueEvent(message);
    }

}
