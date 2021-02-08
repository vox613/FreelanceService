package ru.iteco.project.service.jms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import ru.iteco.project.resource.dto.BookKeepingReportDto;

/**
 * Класс для отправки сообщений в JMS очереди
 */
@Component
public class JmsMessageSender {
    private static final Logger log = LogManager.getLogger(JmsMessageSender.class);

    /*** Вспомогательный класс для отправки сообщений в  JMS*/
    private final JmsTemplate jmsTemplate;

    public JmsMessageSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Метод осуществляет отправку сообщения в очередь с названием queueName
     *
     * @param queueName            - название череди для отправки
     * @param bookKeepingReportDto - сообщение отчед для отправки в сервис бугалтерии
     * @return true-сообщение успешно отправлено, false - возникли проблемы при отправке сообщения в очередь
     */
    public boolean sendBookKeepingReport(final String queueName, final BookKeepingReportDto bookKeepingReportDto) {
        log.info("Sending message {} to queue - {}", bookKeepingReportDto, queueName);
        try {
            jmsTemplate.setTimeToLive(30000);
            jmsTemplate.convertAndSend(queueName, bookKeepingReportDto);
            log.info("Success sended message {} to queue - {}", bookKeepingReportDto, queueName);
            return true;
        } catch (Exception e) {
            log.error(String.format("Error sending message %s to queue - %s", bookKeepingReportDto, queueName), e);
            return false;
        }
    }
}
