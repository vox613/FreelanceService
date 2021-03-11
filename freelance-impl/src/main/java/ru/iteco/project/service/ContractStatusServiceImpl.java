package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.ContractStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidSearchExpressionException;
import ru.iteco.project.repository.ContractRepository;
import ru.iteco.project.repository.ContractStatusRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ContractStatusDtoRequest;
import ru.iteco.project.resource.dto.ContractStatusDtoResponse;
import ru.iteco.project.resource.searching.ContractStatusSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;

/**
 * Класс реализует функционал сервисного слоя для работы со статусами контрактов
 */
@Service
public class ContractStatusServiceImpl implements ContractStatusService {

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;

    /*** Объект доступа к репозиторию контрактов */
    private final ContractRepository contractRepository;

    /*** Объект сервисного слоя контрактов */
    private final ContractService contractService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<ContractStatus> specificationBuilder;

    /*** Объект маппера dto <-> сущность статуса договога */
    private final MapperFacade mapperFacade;


    public ContractStatusServiceImpl(ContractStatusRepository contractStatusRepository, ContractRepository contractRepository,
                                     ContractService contractService, SpecificationBuilder<ContractStatus> specificationBuilder,
                                     MapperFacade mapperFacade) {

        this.contractStatusRepository = contractStatusRepository;
        this.contractRepository = contractRepository;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ContractStatusDtoResponse getContractStatusById(UUID id) {
        ContractStatus contractStatus = contractStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        return mapperFacade.map(contractStatus, ContractStatusDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ContractStatusDtoResponse createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest) {
        checkPossibilityToCreate(contractStatusDtoRequest);
        ContractStatus newContractStatus = mapperFacade.map(contractStatusDtoRequest, ContractStatus.class);
        newContractStatus.setId(UUID.randomUUID());
        ContractStatus save = contractStatusRepository.save(newContractStatus);
        return mapperFacade.map(save, ContractStatusDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ContractStatusDtoResponse updateContractStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest) {
        ContractStatus contractStatus = contractStatusRepository.findById(contractStatusDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkUpdatedData(contractStatusDtoRequest, contractStatus);
        mapperFacade.map(contractStatusDtoRequest, contractStatus);
        ContractStatus save = contractStatusRepository.save(contractStatus);
        return mapperFacade.map(save, ContractStatusDtoResponse.class);
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return List<ContractStatusDtoResponse> - список всех статусов контрактов
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteContractStatus(UUID id) {
        ContractStatus contractStatus = contractStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        contractRepository.findContractsByContractStatus(contractStatus)
                .forEach(contract -> contractService.deleteContract(contract.getId()));
        contractStatusRepository.deleteById(id);
        return true;
    }


    @Override
    public void checkPossibilityToCreate(ContractStatusDtoRequest contractStatusDtoRequest) {
        if (contractStatusRepository.existsContractStatusByValue(contractStatusDtoRequest.getValue())) {
            throw new IllegalArgumentException("errors.persistence.entity.exist");
        }
    }

    @Override
    public void checkUpdatedData(ContractStatusDtoRequest contractStatusDtoRequest, ContractStatus contractStatus) {
        String value = contractStatusDtoRequest.getValue();
        if (!value.equals(contractStatus.getValue())) {
            checkPossibilityToCreate(contractStatusDtoRequest);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ContractStatusDtoResponse> getStatus(SearchDto<ContractStatusSearchDto> searchDto, Pageable pageable) {
        Page<ContractStatus> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = contractStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = contractStatusRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
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
        prepareRestrictionValue(restrictionValues, value, "value", searchUnit -> value.getValue());

        SearchUnit description = contractStatusSearchDto.getDescription();
        prepareRestrictionValue(restrictionValues, description, "description", searchUnit -> description.getValue());

        return restrictionValues;
    }
}
