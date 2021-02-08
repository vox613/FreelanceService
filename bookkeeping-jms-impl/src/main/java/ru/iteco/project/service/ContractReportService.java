package ru.iteco.project.service;

import ru.iteco.project.domain.ContractReport;
import ru.iteco.project.resource.dto.BookKeepingReportDto;

/**
 * Интерфейс сервисного слоя для ContractReport
 */
public interface ContractReportService {

    /**
     * Метод обработчик полученного сообщения из очереди
     *
     * @param bookKeepingReportDto - объект bookKeepingReportDto полученный из очереди
     */
    void proceedQueueEvent(BookKeepingReportDto bookKeepingReportDto);

    /**
     * Метод осуществляет попытку обновления статусов отчетов записанных в БД со статусом, отличным от CONFIRM.
     * Метод используется отложенным заданием для повторной попытки отправки сообщения-подтверждения в сервис-источник
     * отчетов. В случаюх, когда сообщение было получено, записано в БД, но возникли проблемы при отправке подтверждения в очередь JMS.
     *
     * @param contractReport - объект contractReport записанный в БД для которого осуществляется попытка повторной отправки
     * сообщения подтверждения об успешном получении сообщения.
     */
    void renewAckStatus(ContractReport contractReport);


}
