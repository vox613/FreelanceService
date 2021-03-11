package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidSearchExpressionException;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ClientStatusRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientStatusDtoRequest;
import ru.iteco.project.resource.dto.ClientStatusDtoResponse;
import ru.iteco.project.resource.searching.ClientStatusSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;


/**
 * Класс реализует функционал сервисного слоя для работы со статусами пользователей
 */
@Service
public class ClientStatusServiceImpl implements ClientStatusService {

    /*** Объект доступа к репозиторию статусов пользователей */
    private final ClientStatusRepository clientStatusRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект сервисного слоя пользователей */
    private final ClientService clientService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<ClientStatus> specificationBuilder;

    /*** Объект маппера dto <-> сущность статуса пользователя */
    private final MapperFacade mapperFacade;


    public ClientStatusServiceImpl(ClientStatusRepository clientStatusRepository, ClientRepository clientRepository,
                                   ClientService clientService, SpecificationBuilder<ClientStatus> specificationBuilder,
                                   MapperFacade mapperFacade) {
        this.clientStatusRepository = clientStatusRepository;
        this.clientRepository = clientRepository;
        this.clientService = clientService;
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
    public ClientStatusDtoResponse getClientStatusById(UUID id) {
        ClientStatus clientStatus = clientStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        return mapperFacade.map(clientStatus, ClientStatusDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientStatusDtoResponse createClientStatus(ClientStatusDtoRequest clientStatusDtoRequest) {
        checkPossibilityToCreate(clientStatusDtoRequest);
        ClientStatus newClientStatus = mapperFacade.map(clientStatusDtoRequest, ClientStatus.class);
        newClientStatus.setId(UUID.randomUUID());
        ClientStatus save = clientStatusRepository.save(newClientStatus);
        return mapperFacade.map(save, ClientStatusDtoResponse.class);
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientStatusDtoResponse updateClientStatus(UUID id, ClientStatusDtoRequest clientStatusDtoRequest) {
        ClientStatus clientStatusById = clientStatusRepository.findById(clientStatusDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        checkUpdatedData(clientStatusDtoRequest, clientStatusById);
        mapperFacade.map(clientStatusDtoRequest, clientStatusById);
        ClientStatus save = clientStatusRepository.save(clientStatusById);
        return mapperFacade.map(save, ClientStatusDtoResponse.class);
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся статусов пользователей
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientStatusDtoResponse> getAllClientStatuses() {
        return clientStatusRepository.findAll().stream()
                .map(clientStatus -> mapperFacade.map(clientStatus, ClientStatusDtoResponse.class))
                .collect(Collectors.toList());
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteClientStatus(UUID id) {
        ClientStatus clientStatus = clientStatusRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
        );
        clientRepository.findAllByClientStatus(clientStatus)
                .forEach(client -> clientService.deleteClient(client.getId()));
        clientStatusRepository.deleteById(id);
        return true;
    }


    @Override
    public void checkPossibilityToCreate(ClientStatusDtoRequest clientStatusDtoRequest) {
        if (clientStatusRepository.existsClientStatusByValue(clientStatusDtoRequest.getValue())) {
            throw new IllegalArgumentException("errors.persistence.entity.exist");
        }
    }


    @Override
    public void checkUpdatedData(ClientStatusDtoRequest clientStatusDtoRequest, ClientStatus clientStatus) {
        String value = clientStatusDtoRequest.getValue();
        if (!value.equals(clientStatus.getValue())) {
            checkPossibilityToCreate(clientStatusDtoRequest);
        }
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ClientStatusDtoResponse> getStatus(SearchDto<ClientStatusSearchDto> searchDto, Pageable pageable) {
        Page<ClientStatus> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = clientStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = clientStatusRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
        }
        List<ClientStatusDtoResponse> clientStatusDtoResponses = page
                .map(entity -> mapperFacade.map(entity, ClientStatusDtoResponse.class))
                .toList();

        return new PageDto<>(clientStatusDtoResponses, page.getTotalElements(), page.getTotalPages());
    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<ClientStatusSearchDto> searchDto) {
        ClientStatusSearchDto clientStatusSearchDto = searchDto.searchData();
        return new CriteriaObject(clientStatusSearchDto.getJoinOperation(), prepareRestrictionValues(clientStatusSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param statusSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(ClientStatusSearchDto statusSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit value = statusSearchDto.getValue();
        prepareRestrictionValue(restrictionValues, value, "value", searchUnit -> value.getValue());

        SearchUnit description = statusSearchDto.getDescription();
        prepareRestrictionValue(restrictionValues, description, "description", searchUnit -> description.getValue());

        return restrictionValues;
    }

}


