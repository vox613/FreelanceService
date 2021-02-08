package ru.iteco.project.service.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.iteco.project.resource.dto.ContractReportAckMessage;

/**
 * Слушатель JMS событий
 */
@Component
public class JmsMessageListener {
    private static final Logger log = LogManager.getLogger(JmsMessageListener.class);

    /*** Объект сервисного слоя для взаимодействия с сервисом бухгалтерии*/
    private final BookKeepingService bookKeepingService;

    public JmsMessageListener(BookKeepingService bookKeepingService) {
        this.bookKeepingService = bookKeepingService;
    }

    @JmsListener(destination = "${iteco.jms.queue.bookKeepingAckQueue}")
    public void receiveMessage(ContractReportAckMessage message) {
        log.info("Received message {}", message);
        bookKeepingService.proceedAckMessage(message);
    }

}

