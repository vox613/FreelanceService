package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Contract;
import ru.iteco.project.domain.ContractStatus;
import ru.iteco.project.domain.User;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.ContractStatusRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.resource.dto.ContractStatusDtoRequest;
import ru.iteco.project.resource.dto.ContractStatusDtoResponse;
import ru.iteco.project.resource.searching.ContractStatusSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;

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

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<ContractStatus> specificationBuilder;

    /*** Объект маппера dto <-> сущность статуса договога */
    private final MapperFacade mapperFacade;


    public ContractStatusServiceImpl(ContractStatusRepository contractStatusRepository, ContractRepository contractRepository,
                                     UserRepository userRepository, ContractService contractService,
                                     SpecificationBuilder<ContractStatus> specificationBuilder, MapperFacade mapperFacade) {

        this.contractStatusRepository = contractStatusRepository;
        this.contractRepository = contractRepository;
        this.userRepository = userRepository;
        this.contractService = contractService;
        this.specificationBuilder = specificationBuilder;
        this.mapperFacade = mapperFacade;
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
            contractStatusDtoResponse = mapperFacade.map(contractStatus, ContractStatusDtoResponse.class);
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
            ContractStatus newContractStatus = mapperFacade.map(contractStatusDtoRequest, ContractStatus.class);
            newContractStatus.setId(UUID.randomUUID());
            ContractStatus save = contractStatusRepository.save(newContractStatus);
            contractStatusDtoResponse = mapperFacade.map(save, ContractStatusDtoResponse.class);
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

            ContractStatus contractStatus = contractStatusRepository.findById(contractStatusDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

            mapperFacade.map(contractStatusDtoRequest, contractStatus);
            ContractStatus save = contractStatusRepository.save(contractStatus);
            contractStatusDtoResponse = mapperFacade.map(save, ContractStatusDtoResponse.class);
        }
        return contractStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContractStatusDtoResponse> getAllContractsStatuses() {
        return contractStatusRepository.findAll().stream()
                .map(contractStatus -> mapperFacade.map(contractStatus, ContractStatusDtoResponse.class))
                .collect(Collectors.toList());
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

    public PageDto<ContractStatusDtoResponse> getStatus(SearchDto<ContractStatusSearchDto> searchDto, Pageable pageable) {
        Page<ContractStatus> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = contractStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = contractStatusRepository.findAll(pageable);
        }

        List<ContractStatusDtoResponse> ContractStatusDtoResponses = page
                .map(contractStatus -> mapperFacade.map(contractStatus, ContractStatusDtoResponse.class))
                .toList();
        return new PageDto<>(ContractStatusDtoResponses, page.getTotalElements(), page.getTotalPages());

    }


    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<ContractStatusSearchDto> searchDto) {
        ContractStatusSearchDto contractStatusSearchDto = searchDto.searchData();
        return new CriteriaObject(contractStatusSearchDto.getJoinOperation(), prepareRestrictionValues(contractStatusSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param contractStatusSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(ContractStatusSearchDto contractStatusSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit value = contractStatusSearchDto.getValue();
        if (searchUnitIsValid(value)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("value")
                    .setSearchOperation(value.getSearchOperation())
                    .setTypedValue(value.getValue())
                    .build());
        }

        SearchUnit description = contractStatusSearchDto.getDescription();
        if (searchUnitIsValid(description)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("description")
                    .setSearchOperation(description.getSearchOperation())
                    .setValue(description.getValue())
                    .build());
        }
        return restrictionValues;
    }
}
