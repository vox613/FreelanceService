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
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.ClientRepository;
import ru.iteco.project.repository.ClientRoleRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.ClientRoleDtoRequest;
import ru.iteco.project.resource.dto.ClientRoleDtoResponse;
import ru.iteco.project.resource.searching.ClientRoleSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.ADMIN;
import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.isEqualsClientRole;
import static ru.iteco.project.specification.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы с ролями пользователей
 */
@Service
public class ClientRoleServiceImpl implements ClientRoleService {

    private static final Logger log = LogManager.getLogger(ClientRoleServiceImpl.class.getName());

    /*** Объект доступа к репозиторию ролей пользователей */
    private final ClientRoleRepository clientRoleRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект сервисного слоя пользователей */
    private final ClientService clientService;

    /*** Объект маппера dto <-> сущность роль пользователя */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<ClientRole> specificationBuilder;

    public ClientRoleServiceImpl(ClientRoleRepository clientRoleRepository, ClientRepository clientRepository, ClientService clientService,
                                 MapperFacade mapperFacade, SpecificationBuilder<ClientRole> specificationBuilder) {

        this.clientRoleRepository = clientRoleRepository;
        this.clientRepository = clientRepository;
        this.clientService = clientService;
        this.mapperFacade = mapperFacade;
        this.specificationBuilder = specificationBuilder;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientRoleDtoResponse getClientRoleById(UUID id) {
        ClientRoleDtoResponse clientRoleDtoResponse = new ClientRoleDtoResponse();
        Optional<ClientRole> optionalClientRole = clientRoleRepository.findById(id);
        if (optionalClientRole.isPresent()) {
            ClientRole clientRole = optionalClientRole.get();
            clientRoleDtoResponse = mapperFacade.map(clientRole, ClientRoleDtoResponse.class);
        }
        return clientRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientRoleDtoResponse createClientRole(ClientRoleDtoRequest clientRoleDtoRequest) {
        ClientRoleDtoResponse clientRoleDtoResponse = new ClientRoleDtoResponse();
        if (operationIsAllow(clientRoleDtoRequest)) {
            ClientRole newClientRole = mapperFacade.map(clientRoleDtoRequest, ClientRole.class);
            newClientRole.setId(UUID.randomUUID());
            ClientRole save = clientRoleRepository.save(newClientRole);
            clientRoleDtoResponse = mapperFacade.map(save, ClientRoleDtoResponse.class);
        }
        return clientRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    @PreAuthorize("hasRole('ADMIN')")
    public ClientRoleDtoResponse updateClientRole(UUID id, ClientRoleDtoRequest clientRoleDtoRequest) {
        ClientRoleDtoResponse clientRoleDtoResponse = new ClientRoleDtoResponse();
        if (operationIsAllow(clientRoleDtoRequest) &&
                Objects.equals(id, clientRoleDtoRequest.getId()) &&
                clientRoleRepository.existsById(clientRoleDtoRequest.getId())) {

            ClientRole clientRole = clientRoleRepository.findById(clientRoleDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

            mapperFacade.map(clientRoleDtoRequest, clientRole);
            ClientRole save = clientRoleRepository.save(clientRole);
            clientRoleDtoResponse = mapperFacade.map(save, ClientRoleDtoResponse.class);
        }
        return clientRoleDtoResponse;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся ролей пользователей
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public List<ClientRoleDtoResponse> getAllClientsRoles() {
        return clientRoleRepository.findAll().stream()
                .map(clientRole -> mapperFacade.map(clientRole, ClientRoleDtoResponse.class))
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
    public Boolean deleteClientRole(UUID id) {
        Optional<ClientRole> clientRoleOptional = clientRoleRepository.findById(id);
        if (clientRoleOptional.isPresent()) {
            ClientRole clientRole = clientRoleOptional.get();
            Collection<Client> allClientsByRole = clientRepository.findAllByClientRole(clientRole);
            allClientsByRole.forEach(client -> clientService.deleteClient(client.getId()));
            clientRoleRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param clientRoleDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(ClientRoleDtoRequest clientRoleDtoRequest) {
        if ((clientRoleDtoRequest != null) && (clientRoleDtoRequest.getClientId() != null)) {
            Optional<Client> clientOptional = clientRepository.findById(clientRoleDtoRequest.getClientId());
            if (clientOptional.isPresent()) {
                ClientRole role = clientOptional.get().getClientRole();
                return (role != null) && isEqualsClientRole(ADMIN, role.getValue());
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public PageDto<ClientRoleDtoResponse> getRoles(SearchDto<ClientRoleSearchDto> searchDto, Pageable pageable) {
        Page<ClientRole> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = clientRoleRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = clientRoleRepository.findAll(pageable);
        }

        List<ClientRoleDtoResponse> clientRoleDtoResponses = page
                .map(clientRole -> mapperFacade.map(clientRole, ClientRoleDtoResponse.class))
                .toList();
        return new PageDto<>(clientRoleDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<ClientRoleSearchDto> searchDto) {
        ClientRoleSearchDto clientRoleSearchDto = searchDto.searchData();
        return new CriteriaObject(clientRoleSearchDto.getJoinOperation(), prepareRestrictionValues(clientRoleSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param clientRoleSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(ClientRoleSearchDto clientRoleSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit value = clientRoleSearchDto.getValue();
        if (searchUnitIsValid(value)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("value")
                    .setTypedValue(value.getValue())
                    .setSearchOperation(value.getSearchOperation())
                    .build());
        }
        return restrictionValues;
    }

}
