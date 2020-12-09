package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.ContractStatusDtoRequest;
import ru.iteco.project.controller.dto.ContractStatusDtoResponse;
import ru.iteco.project.dao.ContractRepository;
import ru.iteco.project.dao.ContractStatusRepository;
import ru.iteco.project.dao.UserRepository;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.ContractStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.service.mappers.ContractStatusDtoEntityMapper;

import java.util.*;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами контрактов
 */
@Service
public class ContractStatusServiceImpl implements ContractStatusService {

    private static final Logger log = LogManager.getLogger(ContractStatusServiceImpl.class.getName());

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя контрактов */
    private final ContractService contractService;

    /*** Объект маппера dto статуса контракта в сущность статуса контракта */
    private final ContractStatusDtoEntityMapper contractStatusDtoEntityMapper;


    public ContractStatusServiceImpl(ContractStatusRepository contractStatusRepository, ContractRepository contractRepository,
                                     UserRepository userRepository, ContractService contractService,
                                     ContractStatusDtoEntityMapper contractStatusDtoEntityMapper) {

        this.contractStatusRepository = contractStatusRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        this.contractService = contractService;
        this.contractStatusDtoEntityMapper = contractStatusDtoEntityMapper;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public ContractStatusDtoResponse getContractStatusById(UUID id) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        Optional<ContractStatus> optionalContractStatusById = contractStatusRepository.findById(id);
        if (optionalContractStatusById.isPresent()) {
            ContractStatus contractStatus = optionalContractStatusById.get();
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(contractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractStatusDtoResponse createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        if (operationIsAllow(contractStatusDtoRequest)) {
            ContractStatus newContractStatus = contractStatusDtoEntityMapper.requestDtoToEntity(contractStatusDtoRequest);
            contractStatusRepository.save(newContractStatus);
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(newContractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ContractStatusDtoResponse updateContractStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest) {
        ContractStatusDtoResponse contractStatusDtoResponse = new ContractStatusDtoResponse();
        if (operationIsAllow(contractStatusDtoRequest) &&
                Objects.equals(id, contractStatusDtoRequest.getId()) &&
                contractStatusRepository.existsById(contractStatusDtoRequest.getId())) {

            ContractStatus contractStatus = contractStatusDtoEntityMapper.requestDtoToEntity(contractStatusDtoRequest);
            contractStatus.setId(id);
            contractStatusRepository.save(contractStatus);
            contractStatusDtoResponse = contractStatusDtoEntityMapper.entityToResponseDto(contractStatus);
        }
        return contractStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<ContractStatusDtoResponse> getAllContractsStatuses() {
        ArrayList<ContractStatusDtoResponse> contractStatusDtoResponsesList = new ArrayList<>();
        for (ContractStatus contractStatus : contractStatusRepository.findAll()) {
            contractStatusDtoResponsesList.add(contractStatusDtoEntityMapper.entityToResponseDto(contractStatus));
        }
        return contractStatusDtoResponsesList;
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteContractStatus(UUID id) {
        Optional<ContractStatus> contractStatusById = contractStatusRepository.findById(id);
        if (contractStatusById.isPresent()) {
            Collection<Contract> allContractsByStatus = contractRepository.findContractsByContractStatus(contractStatusById.get());
            allContractsByStatus.forEach(contract -> contractService.deleteContract(contract.getId()));
            contractStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param contractStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(ContractStatusDtoRequest contractStatusDtoRequest) {
        if ((contractStatusDtoRequest != null) && (contractStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(contractStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }
}
