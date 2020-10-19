package ru.iteco.project.dao;

import ru.iteco.project.model.Contract;
import ru.iteco.project.model.Task;
import ru.iteco.project.model.User;

import java.util.List;
import java.util.UUID;

public interface ContractDAO extends GenericDAO<Contract, UUID> {

    /**
     * Метод проверяет существует ли в коллекции переданный в аргументах контракт
     *
     * @param contract - контракт, существование которого проверяется
     * @return - true - контракт присутстует в коллекции, false - контракт отсутствует в коллекции
     */
    boolean contractIsExist(Contract contract);

    /**
     * Метод осуществляет поиск контракта по уникальному id
     *
     * @param contractId - уникальный id контракта
     * @return - объект контракта, соответствующий данному id, или null, если контракта нет в коллекции
     */
    Contract findContractById(UUID contractId);

    /**
     * Метод осуществляет поиск контракта по заданию
     *
     * @param task - задание, контракт для которого необходимо найти
     * @return - объект контракта, соответствующий данному заданию, или null, если контракта нет в коллекции
     */
    Contract findContractByTask(Task task);

    /**
     * Метод осуществляет поиск всех договоров данного  исполнителя
     *
     * @param executor - исполнитель, контракты которого необходимо найти
     * @return - список всех договоров исполнителя
     */
    List<Contract> findAllContractsByExecutor(User executor);


}
