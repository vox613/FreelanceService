package ru.iteco.project.service;

import ma.glasnost.orika.MapperFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.repository.UserStatusRepository;
import ru.iteco.project.resource.dto.UserStatusDtoRequest;
import ru.iteco.project.resource.dto.UserStatusDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.resource.searching.UserStatusSearchDto;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы со статусами пользователей
 */
@Service
public class UserStatusServiceImpl implements UserStatusService {

    private static final Logger log = LogManager.getLogger(UserStatusServiceImpl.class.getName());

    /*** Объект доступа к репозиторию статусов пользователей */
    private final UserStatusRepository userStatusRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя пользователей */
    private final UserService userService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<UserStatus> specificationBuilder;

    /*** Объект маппера dto <-> сущность статуса пользователя */
    private final MapperFacade mapperFacade;


    public UserStatusServiceImpl(UserStatusRepository userStatusRepository, UserRepository userRepository,
                                 UserService userService, SpecificationBuilder<UserStatus> specificationBuilder,
                                 MapperFacade mapperFacade) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.specificationBuilder = specificationBuilder;
        this.mapperFacade = mapperFacade;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserStatusDtoResponse getUserStatusById(UUID id) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        Optional<UserStatus> optionalUserStatusById = userStatusRepository.findById(id);
        if (optionalUserStatusById.isPresent()) {
            UserStatus userStatus = optionalUserStatusById.get();
            userStatusDtoResponse = mapperFacade.map(userStatus, UserStatusDtoResponse.class);
        }
        return userStatusDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserStatusDtoResponse createUserStatus(UserStatusDtoRequest userStatusDtoRequest) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        if (operationIsAllow(userStatusDtoRequest)) {
            UserStatus newUserStatus = mapperFacade.map(userStatusDtoRequest, UserStatus.class);
            newUserStatus.setId(UUID.randomUUID());
            UserStatus save = userStatusRepository.save(newUserStatus);
            userStatusDtoResponse = mapperFacade.map(save, UserStatusDtoResponse.class);
        }
        return userStatusDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserStatusDtoResponse updateUserStatus(UUID id, UserStatusDtoRequest userStatusDtoRequest) {
        UserStatusDtoResponse userStatusDtoResponse = new UserStatusDtoResponse();
        if (operationIsAllow(userStatusDtoRequest) &&
                Objects.equals(id, userStatusDtoRequest.getId()) &&
                userStatusRepository.existsById(userStatusDtoRequest.getId())) {

            UserStatus userStatusById = userStatusRepository.findById(userStatusDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
            );
            mapperFacade.map(userStatusDtoRequest, userStatusById);
            UserStatus save = userStatusRepository.save(userStatusById);
            userStatusDtoResponse = mapperFacade.map(save, UserStatusDtoResponse.class);
        }
        return userStatusDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся статусов пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDtoResponse> getAllUsersStatuses() {
        return userStatusRepository.findAll().stream()
                .map(userStatus -> mapperFacade.map(userStatus, UserStatusDtoResponse.class))
                .collect(Collectors.toList());
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUserStatus(UUID id) {
        Optional<UserStatus> userStatusById = userStatusRepository.findById(id);
        if (userStatusById.isPresent()) {
            UserStatus userStatus = userStatusById.get();
            Collection<User> allUsersByStatus = userRepository.findAllByUserStatus(userStatus);
            allUsersByStatus.forEach(user -> userService.deleteUser(user.getId()));
            userStatusRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param userStatusDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(UserStatusDtoRequest userStatusDtoRequest) {
        if ((userStatusDtoRequest != null) && (userStatusDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(userStatusDtoRequest.getUserId());
            if (userById.isPresent()) {
                return isEqualsUserRole(ADMIN, userById.get());
            }
        }
        return false;
    }


    public PageDto<UserStatusDtoResponse> getStatus(SearchDto<UserStatusSearchDto> searchDto, Pageable pageable) {
        Page<UserStatus> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userStatusRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = userStatusRepository.findAll(pageable);
        }
        List<UserStatusDtoResponse> userStatusDtoResponses = page
                .map(entity -> mapperFacade.map(entity, UserStatusDtoResponse.class))
                .toList();

        return new PageDto<>(userStatusDtoResponses, page.getTotalElements(), page.getTotalPages());
    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<UserStatusSearchDto> searchDto) {
        UserStatusSearchDto userStatusSearchDto = searchDto.searchData();
        return new CriteriaObject(userStatusSearchDto.getJoinOperation(), prepareRestrictionValues(userStatusSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param statusSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(UserStatusSearchDto statusSearchDto) {
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


