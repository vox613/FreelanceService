package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.Client;
import ru.iteco.project.domain.ClientStatus;
import ru.iteco.project.exception.*;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ClientRoleRepository;
import ru.iteco.project.repository.ClientStatusRepository;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ClientDtoResponse;
import ru.iteco.project.resource.searching.ClientSearchDto;
import ru.iteco.project.service.util.AuthenticationUtil;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.CREATED;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.isEqualsClientStatus;
import static ru.iteco.project.specification.SpecificationBuilder.prepareRestrictionValue;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class ClientServiceImpl implements ClientService {

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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ClientDtoResponse getClientById(UUID uuid) {
        AuthenticationUtil.checkIdForRole(AuthenticationUtil.ROLE_USER, uuid);
        Client client = clientRepository.findById(uuid).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        return mapperFacade.map(client, ClientDtoResponse.class);
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public ClientDtoResponse createClient(ClientDtoRequest clientDtoRequest) {
        checkPossibilityToCreate(clientDtoRequest);
        Client newClient = mapperFacade.map(clientDtoRequest, Client.class);
        newClient.setId(AuthenticationUtil.getUserPrincipalId());
        Client save = clientRepository.save(newClient);
        return mapperFacade.map(save, ClientDtoResponse.class);
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('USER')")
    public ClientDtoResponse updateClient(ClientDtoRequest clientDtoRequest) {
        Client client = clientRepository.findById(clientDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkUpdatedData(clientDtoRequest, client);
        mapperFacade.map(clientDtoRequest, client);
        Client save = clientRepository.save(client);
        return mapperFacade.map(save, ClientDtoResponse.class);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientDtoResponse updateClientStatus(UUID id, String status) {
        if (!ClientStatus.ClientStatusEnum.isCorrectValue(status)) {
            throw new InvalidClientStatusException("errors.client.status.invalid");
        }
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        ClientStatus clientStatus = clientStatusRepository.findClientStatusByValue(status).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

        client.setClientStatus(clientStatus);
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
    @PreAuthorize("hasAnyRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Boolean deleteClient(UUID id) {
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        AuthenticationUtil.checkIdForRole(AuthenticationUtil.ROLE_USER, client.getId());
        taskRepository.findTasksByClient(client)
                .forEach(task -> taskService.deleteTask(task.getId()));
        clientRepository.deleteById(id);
        return true;
    }


    @Override
    public void checkUpdatedData(ClientDtoRequest clientDtoRequest, Client client) {
        AuthenticationUtil.userIdAndClientIdIsMatched(client.getId());
        String requestEmail = clientDtoRequest.getEmail();
        if (!requestEmail.equals(client.getEmail())) {
            emailIsAvailable(requestEmail);
        }
        String phoneNumber = clientDtoRequest.getPhoneNumber();
        if (!phoneNumber.equals(client.getPhoneNumber())) {
            phoneIsAvailable(phoneNumber);
        }
    }

    @Override
    public void checkPossibilityToCreate(ClientDtoRequest clientDtoRequest) {
        if (!isEqualsClientStatus(CREATED, clientDtoRequest.getClientStatus())) {
            throw new InvalidClientStatusException("errors.client.status.invalid");
        }
        clientIdIsAvailable(AuthenticationUtil.getUserPrincipalId());
        emailIsAvailable(clientDtoRequest.getEmail());
        phoneIsAvailable(clientDtoRequest.getPhoneNumber());
    }

    private void emailIsAvailable(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new NonUniquePersonalDataException("errors.client.email.exist");
        }
    }

    private void phoneIsAvailable(String phoneNumber) {
        if (clientRepository.existsByPhoneNumber(phoneNumber)) {
            throw new NonUniquePersonalDataException("errors.client.phone.exist");
        }
    }

    private void clientIdIsAvailable(UUID id) {
        if (clientRepository.existsById(id)) {
            throw new NonUniquePersonalDataException("errors.client.already.registered");
        }
    }


    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PageDto<ClientDtoResponse> getClients(SearchDto<ClientSearchDto> searchDto, Pageable pageable) {
        Page<Client> page;
        try {
            if ((searchDto != null) && (searchDto.searchData() != null)) {
                page = clientRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
            } else {
                page = clientRepository.findAll(pageable);
            }
        } catch (Exception e) {
            throw new InvalidSearchExpressionException("errors.search.expression.invalid");
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

        SearchUnit role = clientSearchDto.getClientRole();
        prepareRestrictionValue(restrictionValues, role, "clientRole",
                searchUnit -> clientRoleRepository.findClientRoleByValue(role.getValue())
                        .orElseThrow(InvalidClientRoleException::new));

        SearchUnit searchClientStatus = clientSearchDto.getClientStatus();
        prepareRestrictionValue(restrictionValues, searchClientStatus, "clientStatus",
                searchUnit -> clientStatusRepository.findClientStatusByValue(searchClientStatus.getValue())
                        .orElseThrow(InvalidClientStatusException::new));

        SearchUnit secondName = clientSearchDto.getSecondName();
        prepareRestrictionValue(restrictionValues, secondName, "secondName", searchUnit -> secondName.getValue());

        SearchUnit wallet = clientSearchDto.getWallet();
        prepareRestrictionValue(restrictionValues, wallet, "wallet", searchUnit -> wallet.getValue());

        SearchUnit createdAt = clientSearchDto.getCreatedAt();
        prepareRestrictionValue(restrictionValues, createdAt, "createdAt", searchUnit -> createdAt.getValue());

        SearchUnit email = clientSearchDto.getEmail();
        prepareRestrictionValue(restrictionValues, email, "email", searchUnit -> email.getValue());

        return restrictionValues;
    }
}
