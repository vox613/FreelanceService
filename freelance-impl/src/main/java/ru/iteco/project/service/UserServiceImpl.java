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
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.repository.UserRoleRepository;
import ru.iteco.project.repository.UserStatusRepository;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.resource.searching.UserSearchDto;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserStatus.UserStatusEnum.CREATED;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.isEqualsUserStatus;
import static ru.iteco.project.service.specifications.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию ролей пользователей */
    private final UserRoleRepository userRoleRepository;

    /*** Объект доступа к репозиторию статусов пользователей */
    private final UserStatusRepository userStatusRepository;

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<User> specificationBuilder;

    /*** Объект маппера dto <-> сущность пользователя */
    private final MapperFacade mapperFacade;


    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, UserRoleRepository userRoleRepository,
                           UserStatusRepository userStatusRepository, TaskService taskService,
                           SpecificationBuilder<User> specificationBuilder, MapperFacade mapperFacade) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatusRepository = userStatusRepository;
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
    public UserDtoResponse getUserById(UUID uuid) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        Optional<User> optionalUser = userRepository.findById(uuid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userDtoResponse = mapperFacade.map(user, UserDtoResponse.class);
        }
        return userDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDtoResponse createUser(UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (isCorrectLoginEmail(userDtoRequest.getLogin(), userDtoRequest.getEmail())
                && isEqualsUserStatus(CREATED, userDtoRequest.getUserStatus())) {
            User newUser = mapperFacade.map(userDtoRequest, User.class);
            newUser.setId(UUID.randomUUID());
            User save = userRepository.save(newUser);
            userDtoResponse = mapperFacade.map(save, UserDtoResponse.class);
        }
        return userDtoResponse;
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<UserDtoResponse> createBundleUsers(List<UserDtoRequest> userDtoRequestList) {
        List<User> usersList = userDtoRequestList.stream()
                .map(requestDto -> {
                    User mappedUser = mapperFacade.map(requestDto, User.class);
                    mappedUser.setId(UUID.randomUUID());
                    return mappedUser;
                })
                .collect(Collectors.toList());

        List<User> users = userRepository.saveAll(usersList);
        return users.stream()
                .map(entity -> mapperFacade.map(entity, UserDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserDtoResponse updateUser(UserDtoRequest userDtoRequest) {
        UserDtoResponse userDtoResponse = null;
        if (userRepository.existsById(userDtoRequest.getId())) {
            User user = userRepository.findById(userDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
            mapperFacade.map(userDtoRequest, user);
            User save = userRepository.save(user);
            userDtoResponse = mapperFacade.map(save, UserDtoResponse.class);
        }
        return userDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDtoResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapperFacade.map(user, UserDtoResponse.class))
                .collect(Collectors.toList());
    }

    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            taskRepository.findTasksByUser(user)
                    .forEach(task -> taskService.deleteTask(task.getId()));
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет что пользователя с таким логином и email не существует
     *
     * @param login - логин пользователя
     * @param email - email пользователя
     * @return - true - логин и email не пусты и пользователя с такими данными не существует, false - в любом ином случае
     */
    private boolean isCorrectLoginEmail(String login, String email) {
        return !userRepository.existsByEmailOrLogin(email, login);
    }

    @Override
    public PageDto<UserDtoResponse> getUsers(SearchDto<UserSearchDto> searchDto, Pageable pageable) {
        Page<User> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        List<UserDtoResponse> userDtoResponses = page.map(entity -> mapperFacade.map(entity, UserDtoResponse.class)).toList();
        return new PageDto<>(userDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<UserSearchDto> searchDto) {
        UserSearchDto userSearchDto = searchDto.searchData();
        return new CriteriaObject(userSearchDto.getJoinOperation(), prepareRestrictionValues(userSearchDto));
    }


    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param userSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(UserSearchDto userSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit role = userSearchDto.getRole();
        if (searchUnitIsValid(role)) {
            UserRole userRole = userRoleRepository.findUserRoleByValue(role.getValue())
                    .orElseThrow(InvalidUserRoleException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("role")
                    .setSearchOperation(role.getSearchOperation())
                    .setTypedValue(userRole)
                    .build());
        }


        SearchUnit searchUserStatus = userSearchDto.getUserStatus();
        if (searchUnitIsValid(searchUserStatus)) {
            UserStatus userStatus = userStatusRepository.findUserStatusByValue(searchUserStatus.getValue())
                    .orElseThrow(InvalidUserStatusException::new);

            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("userStatus")
                    .setSearchOperation(searchUserStatus.getSearchOperation())
                    .setTypedValue(userStatus)
                    .build());
        }

        SearchUnit secondName = userSearchDto.getSecondName();
        if (searchUnitIsValid(secondName)) {
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("secondName")
                    .setSearchOperation(secondName.getSearchOperation())
                    .setValue(secondName.getValue())
                    .build());
        }

        SearchUnit wallet = userSearchDto.getWallet();
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

        SearchUnit createdAt = userSearchDto.getCreatedAt();
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
