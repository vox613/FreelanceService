package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Client;
import ru.iteco.project.domain.ClientRole;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
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

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.ADMIN;
import static ru.iteco.project.specification.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы со статусами пользователей
 */
@Service
public class ClientStatusServiceImpl implements ClientStatusService {

    private static final Logger log = LogManager.getLogger(ClientStatusServiceImpl.class.getName());

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
        ClientStatusDtoResponse clientStatusDtoResponse = new ClientStatusDtoResponse();
        Optional<ClientStatus> clientStatusOptional = clientStatusRepository.findById(id);
        if (clientStatusOptional.isPresent()) {
            ClientStatus clientStatus = clientStatusOptional.get();
            clientStatusDtoResponse = mapperFacade.map(clientStatus, ClientStatusDtoResponse.class);
        }
        return clientStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientStatusDtoResponse createClientStatus(ClientStatusDtoRequest clientStatusDtoRequest) {
        ClientStatusDtoResponse clientStatusDtoResponse = new ClientStatusDtoResponse();
        if (operationIsAllow(clientStatusDtoRequest)) {
            ClientStatus newClientStatus = mapperFacade.map(clientStatusDtoRequest, ClientStatus.class);
            newClientStatus.setId(UUID.randomUUID());
            ClientStatus save = clientStatusRepository.save(newClientStatus);
            clientStatusDtoResponse = mapperFacade.map(save, ClientStatusDtoResponse.class);
        }
        return clientStatusDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientStatusDtoResponse updateClientStatus(UUID id, ClientStatusDtoRequest clientStatusDtoRequest) {
        ClientStatusDtoResponse clientStatusDtoResponse = new ClientStatusDtoResponse();
        if (operationIsAllow(clientStatusDtoRequest) &&
                Objects.equals(id, clientStatusDtoRequest.getId()) &&
                clientStatusRepository.existsById(clientStatusDtoRequest.getId())) {

            ClientStatus clientStatusById = clientStatusRepository.findById(clientStatusDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
            );
            mapperFacade.map(clientStatusDtoRequest, clientStatusById);
            ClientStatus save = clientStatusRepository.save(clientStatusById);
            clientStatusDtoResponse = mapperFacade.map(save, ClientStatusDtoResponse.class);
        }
        return clientStatusDtoResponse;
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
        Optional<ClientStatus> clientStatusById = clientStatusRepository.findById(id);
        if (clientStatusById.isPresent()) {
            ClientStatus clientStatus = clientStatusById.get();
            Collection<Client> allByClientStatus = clientRepository.findAllByClientStatus(clientStatus);
            allByClientStatus.forEach(client -> clientService.deleteClient(client.getId()));
            clientStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param clientStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(ClientStatusDtoRequest clientStatusDtoRequest) {
        if ((clientStatusDtoRequest != null) && (clientStatusDtoRequest.getClientId() != null)) {
            Optional<Client> client = clientRepository.findById(clientStatusDtoRequest.getClientId());
            if (client.isPresent()) {
                return ClientRole.ClientRoleEnum.isEqualsClientRole(ADMIN, client.get());
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ClientStatusDtoResponse> getStatus(SearchDto<ClientStatusSearchDto> searchDto, Pageable pageable) {
        Page<ClientStatus> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = clientStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = clientStatusRepository.findAll(pageable);
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
        if (searchUnitIsValid(value)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("value")
                    .setTypedValue(value.getValue())
                    .setSearchOperation(value.getSearchOperation())
                    .build());
        }

        SearchUnit description = statusSearchDto.getDescription();
        if (searchUnitIsValid(description)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("description")
                    .setValue(description.getValue())
                    .setSearchOperation(description.getSearchOperation())
                    .build());
        }
        return restrictionValues;
    }

}


