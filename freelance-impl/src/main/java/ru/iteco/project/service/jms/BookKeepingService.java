package ru.iteco.project.service.jms;

import ru.iteco.project.domain.Contract;
import ru.iteco.project.resource.dto.ContractReportAckMessage;

/**
 * Интерфейс для взаимодействия с сервисом бухгалтерии
 */
public interface BookKeepingService {

    /**
     * Сигнатура метода для отправки выполенного контракта в сервис бухгалтерии
     *
     * @param contract - сущность контракта
     */
    void sendReportToBookKeeping(Contract contract);

    /**
     * Сигнатура метода обработки сообщения-подтверждения об успешном получении отчета системой бухгалтерии
     *
     * @param message - сообщени подтверждения со статусом получения сообщения
     */
    void proceedAckMessage(ContractReportAckMessage message);

}
