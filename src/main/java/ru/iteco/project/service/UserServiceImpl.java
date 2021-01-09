package ru.iteco.project.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.SearchDto;
import ru.iteco.project.controller.searching.SearchUnit;
import ru.iteco.project.controller.searching.UserSearchDto;
import ru.iteco.project.domain.User;
import ru.iteco.project.domain.UserRole;
import ru.iteco.project.domain.UserStatus;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.repository.TaskRepository;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.service.mappers.UserDtoEntityMapper;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    /*** Объект сервисного слоя заданий */
    private final TaskService taskService;

    /*** Объект маппера dto пользователя в сущность пользователя */
    private final UserDtoEntityMapper userMapper;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<User> specificationBuilder;

    public UserServiceImpl(UserRepository userRepository, TaskRepository taskRepository, TaskService taskService,
                           UserDtoEntityMapper userMapper, SpecificationBuilder<User> specificationBuilder) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.userMapper = userMapper;
        this.specificationBuilder = specificationBuilder;
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
            userDtoResponse = userMapper.entityToResponseDto(user);
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
            User newUser = userMapper.requestDtoToEntity(userDtoRequest);
            User save = userRepository.save(newUser);
            userDtoResponse = userMapper.entityToResponseDto(save);
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
        List<User> usersList = userDtoRequestList.stream().map(userMapper::requestDtoToEntity).collect(Collectors.toList());
        List<User> users = userRepository.saveAll(usersList);
        return users.stream().map(userMapper::entityToResponseDto).collect(Collectors.toList());
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
            User user = userMapper.requestDtoToEntity(userDtoRequest);
            user.setId(userDtoRequest.getId());
            User save = userRepository.save(user);
            userDtoResponse = userMapper.entityToResponseDto(save);
        }
        return userDtoResponse;
    }

    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     */
    @Override
    @Transactional(readOnly = true)
    public ArrayList<UserDtoResponse> getAllUsers() {
        ArrayList<UserDtoResponse> userDtoResponseList = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtoResponseList.add(userMapper.entityToResponseDto(user));
        }
        return userDtoResponseList;
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


    public UserDtoEntityMapper getUserMapper() {
        return userMapper;
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


    public PageDto<UserDtoResponse> getUsers(SearchDto<UserSearchDto> searchDto, Pageable pageable) {
        Page<User> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        List<UserDtoResponse> userDtoResponses = page.map(userMapper::entityToResponseDto).toList();
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
            UserRole userRole = userMapper.getUserRoleRepository()
                    .findUserRoleByValue(role.getValue())
                    .orElseThrow(InvalidUserRoleException::new);

            restrictionValues.add(
                    new CriteriaObject.RestrictionValues<UserRole>(
                            "role",
                            role.getSearchOperation(),
                            userRole
                    )
            );
        }

        SearchUnit searchUserStatus = userSearchDto.getUserStatus();
        if (searchUnitIsValid(searchUserStatus)) {
            UserStatus userStatus = userMapper.getUserStatusRepository()
                    .findUserStatusByValue(searchUserStatus.getValue())
                    .orElseThrow(InvalidUserStatusException::new);

            restrictionValues.add(
                    new CriteriaObject.RestrictionValues<UserStatus>(
                            "userStatus",
                            searchUserStatus.getSearchOperation(),
                            userStatus
                    )
            );
        }

        SearchUnit secondName = userSearchDto.getSecondName();
        if (searchUnitIsValid(secondName)) {
            restrictionValues.add(
                    new CriteriaObject.RestrictionValues<String>(
                            "secondName",
                            secondName.getValue(),
                            secondName.getSearchOperation()
                    )
            );
        }

        SearchUnit wallet = userSearchDto.getWallet();
        if (searchUnitIsValid(wallet)) {
            if (isBetweenOperation(wallet)) {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<BigDecimal>(
                                "wallet",
                                wallet.getSearchOperation(),
                                wallet.getValue(),
                                wallet.getMinValue(),
                                wallet.getMaxValue()
                        )
                );
            } else {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<BigDecimal>(
                                "wallet",
                                wallet.getValue(),
                                wallet.getSearchOperation()
                        )
                );
            }
        }

        SearchUnit createdAt = userSearchDto.getCreatedAt();
        if (searchUnitIsValid(createdAt)) {
            if (isBetweenOperation(createdAt)) {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "createdAt",
                                createdAt.getSearchOperation(),
                                createdAt.getValue(),
                                createdAt.getMinValue(),
                                createdAt.getMaxValue()
                        )
                );
            } else {
                restrictionValues.add(
                        new CriteriaObject.RestrictionValues<LocalDateTime>(
                                "createdAt",
                                createdAt.getValue(),
                                createdAt.getSearchOperation()
                        )
                );
            }
        }
        return restrictionValues;
    }
}
