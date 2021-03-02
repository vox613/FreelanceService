package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Client;
import ru.iteco.project.domain.ClientRole;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidClientRoleException;
import ru.iteco.project.exception.InvalidClientStatusException;
import ru.iteco.project.exception.NonUniquePersonalDataException;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ClientRoleRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.ClientStatusRepository;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ClientDtoResponse;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.searching.ClientSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.CREATED;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.isEqualsClientStatus;
import static ru.iteco.project.specification.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.specification.SpecificationBuilder.searchUnitIsValid;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LogManager.getLogger(ClientServiceImpl.class.getName());

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию ролей пользователей */
    private final ClientRoleRepository clientRoleRepository;

    /*** Объект доступа к репозиторию статусов пользователей */
    private final ClientStatusRepository clientStatusRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<Client> specificationBuilder;

    /*** Объект маппера dto <-> сущность пользователя */
    private final MapperFacade mapperFacade;


    public ClientServiceImpl(ClientRepository clientRepository, TaskRepository taskRepository, ClientRoleRepository clientRoleRepository,
                             ClientStatusRepository clientStatusRepository, TaskService taskService,
                             SpecificationBuilder<Client> specificationBuilder, MapperFacade mapperFacade) {
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
        this.clientRoleRepository = clientRoleRepository;
        this.clientStatusRepository = clientStatusRepository;
        this.taskService = taskService;
        this.specificationBuilder = specificationBuilder;
        this.mapperFacade = mapperFacade;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public ClientDtoResponse getClientById(UUID uuid) {
        ClientDtoResponse clientDtoResponse = new ClientDtoResponse();
        Optional<Client> optionalClient = clientRepository.findById(uuid);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            clientDtoResponse = mapperFacade.map(client, ClientDtoResponse.class);
        }
        return clientDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientDtoResponse createClient(ClientDtoRequest clientDtoRequest) {
        ClientDtoResponse clientDtoResponse = null;
        if (isEqualsClientStatus(CREATED, clientDtoRequest.getClientStatus())) {
            emailIsAvailable(clientDtoRequest.getEmail());
            Client newClient = mapperFacade.map(clientDtoRequest, Client.class);
            newClient.setId(UUID.randomUUID());
            Client save = clientRepository.save(newClient);
            clientDtoResponse = mapperFacade.map(save, ClientDtoResponse.class);
        }
        return clientDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<ClientDtoResponse> createBundleClients(List<ClientDtoRequest> clientDtoRequestList) {
        List<Client> clientList = clientDtoRequestList.stream()
                .map(requestDto -> {
                    emailIsAvailable(requestDto.getEmail());
                    Client mappedClient = mapperFacade.map(requestDto, Client.class);
                    mappedClient.setId(UUID.randomUUID());
                    return mappedClient;
                })
                .collect(Collectors.toList());

        List<Client> clients = clientRepository.saveAll(clientList);
        return clients.stream()
                .map(entity -> mapperFacade.map(entity, ClientDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientDtoResponse updateClient(ClientDtoRequest clientDtoRequest) {
        Client client = clientRepository.findById(clientDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkUpdatedData(clientDtoRequest, client);
        mapperFacade.map(clientDtoRequest, client);
        Client save = clientRepository.save(client);
        return mapperFacade.map(save, ClientDtoResponse.class);
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<ClientDtoResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> mapperFacade.map(client, ClientDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteClient(UUID id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            taskRepository.findTasksByClient(client)
                    .forEach(task -> taskService.deleteTask(task.getId()));
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }


    private void checkUpdatedData(ClientDtoRequest clientDtoRequest, Client client) {
        String requestEmail = clientDtoRequest.getEmail();
        if(!requestEmail.equals(client.getEmail()) && clientRepository.existsByEmail(requestEmail)){
            throw new NonUniquePersonalDataException("errors.client.email.exist");
        }
    }

    private void emailIsAvailable(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new NonUniquePersonalDataException("errors.persistence.entity.exist");
        }
    }


    @Override
    public PageDto<ClientDtoResponse> getClients(SearchDto<ClientSearchDto> searchDto, Pageable pageable) {
        Page<Client> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = clientRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = clientRepository.findAll(pageable);
        }

        List<ClientDtoResponse> clientDtoResponses = page.map(entity -> mapperFacade.map(entity, ClientDtoResponse.class)).toList();
        return new PageDto<>(clientDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<ClientSearchDto> searchDto) {
        ClientSearchDto clientSearchDto = searchDto.searchData();
        return new CriteriaObject(clientSearchDto.getJoinOperation(), prepareRestrictionValues(clientSearchDto));
    }


    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param clientSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(ClientSearchDto clientSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit role = clientSearchDto.getRole();
        if (searchUnitIsValid(role)) {
            ClientRole clientRole = clientRoleRepository.findClientRoleByValue(role.getValue())
                    .orElseThrow(InvalidClientRoleException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("role")
                    .setSearchOperation(role.getSearchOperation())
                    .setTypedValue(clientRole)
                    .build());
        }


        SearchUnit searchClientStatus = clientSearchDto.getClientStatus();
        if (searchUnitIsValid(searchClientStatus)) {
            ClientStatus clientStatus = clientStatusRepository.findClientStatusByValue(searchClientStatus.getValue())
                    .orElseThrow(InvalidClientStatusException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("clientStatus")
                    .setSearchOperation(searchClientStatus.getSearchOperation())
                    .setTypedValue(clientStatus)
                    .build());
        }

        SearchUnit secondName = clientSearchDto.getSecondName();
        if (searchUnitIsValid(secondName)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("secondName")
                    .setSearchOperation(secondName.getSearchOperation())
                    .setValue(secondName.getValue())
                    .build());
        }

        SearchUnit wallet = clientSearchDto.getWallet();
        if (searchUnitIsValid(wallet)) {
            if (isBetweenOperation(wallet)) {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("wallet")
                        .setSearchOperation(wallet.getSearchOperation())
                        .setValue(wallet.getValue())
                        .setMinValue(wallet.getMinValue())
                        .setMaxValue(wallet.getMaxValue())
                        .build());
            } else {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("wallet")
                        .setValue(wallet.getValue())
                        .setSearchOperation(wallet.getSearchOperation())
                        .build());
            }
        }

        SearchUnit createdAt = clientSearchDto.getCreatedAt();
        if (searchUnitIsValid(createdAt)) {
            if (isBetweenOperation(createdAt)) {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("createdAt")
                        .setSearchOperation(createdAt.getSearchOperation())
                        .setValue(createdAt.getValue())
                        .setMinValue(createdAt.getMinValue())
                        .setMaxValue(createdAt.getMaxValue())
                        .build());
            } else {
                restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                        .setKey("createdAt")
                        .setValue(createdAt.getValue())
                        .setSearchOperation(createdAt.getSearchOperation())
                        .build());
            }
        }
        return restrictionValues;
    }
}
