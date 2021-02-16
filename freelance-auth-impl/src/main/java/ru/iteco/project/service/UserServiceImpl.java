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
import ru.iteco.project.exception.*;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.SearchDto;
import ru.iteco.project.resource.SearchUnit;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.resource.searching.UserSearchDto;
import ru.iteco.project.specification.CriteriaObject;
import ru.iteco.project.specification.SpecificationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.iteco.project.specification.SpecificationBuilder.isBetweenOperation;
import static ru.iteco.project.specification.SpecificationBuilder.searchUnitIsValid;

/**
 * Класс реализует функционал сервисного слоя для работы с пользователями
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class.getName());

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<User> specificationBuilder;

    /*** Объект маппера dto <-> сущность пользователя */
    private final MapperFacade mapperFacade;


    public UserServiceImpl(UserRepository userRepository, SpecificationBuilder<User> specificationBuilder,
                           MapperFacade mapperFacade) {
        this.userRepository = userRepository;
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
        isCorrectUsernameEmail(userDtoRequest.getUsername(), userDtoRequest.getEmail());

        User newUser = mapperFacade.map(userDtoRequest, User.class);
        newUser.setId(UUID.randomUUID());
        User save = userRepository.save(newUser);
        return mapperFacade.map(save, UserDtoResponse.class);
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
                    isCorrectUsernameEmail(requestDto.getUsername(), requestDto.getEmail());
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
        User user = userRepository.findById(userDtoRequest.getId()).orElseThrow(
                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));
        checkingUpdatedData(userDtoRequest, user);

        mapperFacade.map(userDtoRequest, user);
        User save = userRepository.save(user);
        return mapperFacade.map(save, UserDtoResponse.class);
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
            user.setStatus(UserStatus.DELETED);
            userRepository.save(user);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет что пользователя с таким логином и email не существует
     *
     * @param username - логин пользователя
     * @param email    - email пользователя
     */
    private void isCorrectUsernameEmail(String username, String email) {
        if (userRepository.existsByEmailOrUsername(email, username)) {
            throw new NonUniquePersonalDataException("errors.persistence.entity.exist");
        }
    }

    private void checkingUpdatedData(UserDtoRequest userDtoRequest, User user) {
        if (!user.getUsername().equals(userDtoRequest.getUsername())) {
            throw new UnavailableOperationException("errors.user.username.update");
        }

        String requestEmail = userDtoRequest.getEmail();
        if (!user.getEmail().equals(requestEmail) && userRepository.existsByEmail(requestEmail)) {
            throw new NonUniquePersonalDataException("errors.user.email.exist");
        }
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
            if (!UserRole.isCorrectValue(role.getValue())) {
                throw new InvalidUserRoleException(role.getValue() + " - not valid value!");
            }
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("role")
                    .setSearchOperation(role.getSearchOperation())
                    .setTypedValue(UserRole.valueOf(role.getValue()))
                    .build());
        }


        SearchUnit searchUserStatus = userSearchDto.getStatus();
        if (searchUnitIsValid(searchUserStatus)) {
            if (!UserStatus.isCorrectValue(searchUserStatus.getValue())) {
                throw new InvalidUserStatusException(searchUserStatus.getValue() + " - not valid value!");
            }
            restrictionValues.add(CriteriaObject.RestrictionValues.newBuilder()
                    .setKey("status")
                    .setSearchOperation(searchUserStatus.getSearchOperation())
                    .setTypedValue(UserStatus.valueOf(searchUserStatus.getValue()))
                    .build());
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
