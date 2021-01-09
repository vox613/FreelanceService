package ru.iteco.project.service.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.iteco.project.controller.dto.ContractDtoRequest;
import ru.iteco.project.controller.dto.ContractDtoResponse;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.Task;
import ru.iteco.project.domain.TaskStatus;
import ru.iteco.project.exception.InvalidContractStatusException;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.repository.ContractStatusRepository;
import ru.iteco.project.repository.TaskStatusRepository;

import java.util.Optional;
import java.util.UUID;

import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.PAID;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.CUSTOMER;

/**
 * Класс маппер для сущности Contract
 */
@Service
@PropertySource(value = {"classpath:application.yml", "classpath:errors.properties"})
public class ContractDtoEntityMapper implements DtoEntityMapper<Contract, ContractDtoRequest, ContractDtoResponse> {

    @Value("${errors.contract.status.invalid}")
    private String invalidContractStatusMessage;

    @Value("${errors.task.status.invalid}")
    private String taskStatusIsInvalidMessage;

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;


    public ContractDtoEntityMapper(ContractStatusRepository contractStatusRepository, TaskStatusRepository taskStatusRepository) {
        this.contractStatusRepository = contractStatusRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public ContractDtoResponse entityToResponseDto(Contract entity) {
        final ContractDtoResponse contractDtoResponse = new ContractDtoResponse();
        if (entity != null) {
            contractDtoResponse.setId(entity.getId());
            contractDtoResponse.setExecutorId(entity.getExecutor().getId());
            contractDtoResponse.setTaskId(entity.getTask().getId());
            contractDtoResponse.setCreatedAt(DateTimeMapper.objectToString(entity.getCreatedAt()));
            contractDtoResponse.setUpdatedAt(DateTimeMapper.objectToString(entity.getUpdatedAt()));
            contractDtoResponse.setContractStatus(entity.getContractStatus().getValue());
        }
        return contractDtoResponse;
    }

    @Override
    public Contract requestDtoToEntity(ContractDtoRequest requestDto) {
        Contract contract = new Contract();
        if (requestDto != null) {
            contract.setId(UUID.randomUUID());
            contract.setContractStatus(contractStatusRepository.findContractStatusByValue(PAID.name())
                    .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
        }
        return contract;
    }

    /**
     * Маппер для обновления заказчиком статуса договора
     *
     * @param requestDto - объект запроса
     * @param contract   - сущность договора
     * @param role       - роль пользователя инициировавшего процесс
     */
    public void requestDtoToEntity(ContractDtoRequest requestDto, Contract contract, String role) {
        if (requestDto != null) {
            if (CUSTOMER.name().equals(role)) {
                contract.setContractStatus(contractStatusRepository.findContractStatusByValue(requestDto.getContractStatus())
                        .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
            }
        }
    }

    /**
     * Метод устанавливает переданному заданию статус соответствующий переданному объекту taskStatusEnum
     *
     * @param task           - сущность задания
     * @param taskStatusEnum - объект перечисления статусов заданий
     */
    public void updateTaskStatus(Task task, TaskStatus.TaskStatusEnum taskStatusEnum) {
        Optional<TaskStatus> taskStatusByValue = taskStatusRepository.findTaskStatusByValue(taskStatusEnum.name());
        TaskStatus taskStatus = taskStatusByValue
                .orElseThrow(() -> new InvalidTaskStatusException(taskStatusIsInvalidMessage));
        task.setTaskStatus(taskStatus);
    }

    public ContractStatusRepository getContractStatusRepository() {
        return contractStatusRepository;
    }
}
