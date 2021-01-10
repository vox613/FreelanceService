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
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.repository.UserRepository;
import ru.iteco.project.repository.UserRoleRepository;
import ru.iteco.project.resource.dto.UserRoleDtoRequest;
import ru.iteco.project.resource.dto.UserRoleDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.SearchDto;
import ru.iteco.project.resource.searching.SearchUnit;
import ru.iteco.project.resource.searching.UserRoleSearchDto;
import ru.iteco.project.service.specifications.CriteriaObject;
import ru.iteco.project.service.specifications.SpecificationBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static ru.iteco.project.domain.UserRole.UserRoleEnum.ADMIN;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.isEqualsUserRole;
import static ru.iteco.project.service.specifications.SpecificationBuilder.searchUnitIsValid;


/**
 * Класс реализует функционал сервисного слоя для работы с ролями пользователей
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private static final Logger log = LogManager.getLogger(UserRoleServiceImpl.class.getName());

    /*** Объект доступа к репозиторию ролей пользователей */
    private final UserRoleRepository userRoleRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект сервисного слоя пользователей */
    private final UserService userService;

    /*** Объект маппера dto <-> сущность роль пользователя */
    private final MapperFacade mapperFacade;

    /*** Сервис для формирования спецификации поиска данных */
    private final SpecificationBuilder<UserRole> specificationBuilder;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, UserService userService,
                               MapperFacade mapperFacade, SpecificationBuilder<UserRole> specificationBuilder) {

        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mapperFacade = mapperFacade;
        this.specificationBuilder = specificationBuilder;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(readOnly = true)
    public UserRoleDtoResponse getUserRoleById(UUID id) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        Optional<UserRole> optionalUserRoleById = userRoleRepository.findById(id);
        if (optionalUserRoleById.isPresent()) {
            UserRole userRole = optionalUserRoleById.get();
            userRoleDtoResponse = mapperFacade.map(userRole, UserRoleDtoResponse.class);
        }
        return userRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserRoleDtoResponse createUserRole(UserRoleDtoRequest userRoleDtoRequest) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        if (operationIsAllow(userRoleDtoRequest)) {
            UserRole newUserRole = mapperFacade.map(userRoleDtoRequest, UserRole.class);
            newUserRole.setId(UUID.randomUUID());
            UserRole save = userRoleRepository.save(newUserRole);
            userRoleDtoResponse = mapperFacade.map(save, UserRoleDtoResponse.class);
        }
        return userRoleDtoResponse;
    }


    /**
     * SERIALIZABLE - т.к. во время модификации и создание новых данных не должно быть влияния извне
     * REQUIRED - в транзакции внешней или новой
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserRoleDtoResponse updateUserRole(UUID id, UserRoleDtoRequest userRoleDtoRequest) {
        UserRoleDtoResponse userRoleDtoResponse = new UserRoleDtoResponse();
        if (operationIsAllow(userRoleDtoRequest) &&
                Objects.equals(id, userRoleDtoRequest.getId()) &&
                userRoleRepository.existsById(userRoleDtoRequest.getId())) {

            UserRole userRole = userRoleRepository.findById(userRoleDtoRequest.getId()).orElseThrow(
                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound"));

            mapperFacade.map(userRoleDtoRequest, userRole);
            UserRole save = userRoleRepository.save(userRole);
            userRoleDtoResponse = mapperFacade.map(save, UserRoleDtoResponse.class);
        }
        return userRoleDtoResponse;
    }


    /**
     * По умолчанию в Postgres isolation READ_COMMITTED + недоступна модификация данных
     *
     * @return список всех имеющихся ролей пользователей
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserRoleDtoResponse> getAllUsersRoles() {
        return userRoleRepository.findAll().stream()
                .map(userRole -> mapperFacade.map(userRole, UserRoleDtoResponse.class))
                .collect(Collectors.toList());
    }


    /**
     * SERIALIZABLE - во время удаления внешние тразнзакции не должны иметь никакого доступа к записи
     * REQUIRED - в транзакции внешней или новой, т.к. используется в других сервисах при удалении записей и
     * должна быть применена только при выполнении общей транзакции (единицы бизнес логики)
     */
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Boolean deleteUserRole(UUID id) {
        Optional<UserRole> userRoleById = userRoleRepository.findById(id);
        if (userRoleById.isPresent()) {
            UserRole userRole = userRoleById.get();
            Collection<User> allUsersByRole = userRepository.findAllByRole(userRole);
            allUsersByRole.forEach(user -> userService.deleteUser(user.getId()));
            userRoleRepository.deleteById(id);
            return true;
        }
        return false;
    }


    /**
     * Метод проверяет разрешена ли для пользователя данная операция
     *
     * @param userRoleDtoRequest - запрос
     * @return true - операция разрешена, false - операция запрещена
     */
    private boolean operationIsAllow(UserRoleDtoRequest userRoleDtoRequest) {
        if ((userRoleDtoRequest != null) && (userRoleDtoRequest.getUserId() != null)) {
            Optional<User> userById = userRepository.findById(userRoleDtoRequest.getUserId());
            if (userById.isPresent()) {
                UserRole role = userById.get().getRole();
                return (role != null) && isEqualsUserRole(ADMIN, role.getValue());
            }
        }
        return false;
    }

    public PageDto<UserRoleDtoResponse> getRoles(SearchDto<UserRoleSearchDto> searchDto, Pageable pageable) {
        Page<UserRole> page;
        if ((searchDto != null) && (searchDto.searchData() != null)) {
            page = userRoleRepository.findAll(specificationBuilder.getSpec(prepareCriteriaObject(searchDto)), pageable);
        } else {
            page = userRoleRepository.findAll(pageable);
        }

        List<UserRoleDtoResponse> userRoleDtoResponses = page
                .map(userRole -> mapperFacade.map(userRole, UserRoleDtoResponse.class))
                .toList();
        return new PageDto<>(userRoleDtoResponses, page.getTotalElements(), page.getTotalPages());

    }

    /**
     * Метод наполняет CriteriaObject данными поиска из searchDto
     *
     * @param searchDto - модель с данными для поиска
     * @return - CriteriaObject - конейнер со всеми данными и ограничениями для поиска
     */
    private CriteriaObject prepareCriteriaObject(SearchDto<UserRoleSearchDto> searchDto) {
        UserRoleSearchDto userRoleSearchDto = searchDto.searchData();
        return new CriteriaObject(userRoleSearchDto.getJoinOperation(), prepareRestrictionValues(userRoleSearchDto));
    }

    /**
     * Метод подготавливает ограничения для полей поиска
     *
     * @param userRoleSearchDto - модель с данными для поиска
     * @return - мписок ограничений для всех полей по которым осуществляется поиск
     */
    private List<CriteriaObject.RestrictionValues> prepareRestrictionValues(UserRoleSearchDto userRoleSearchDto) {
        ArrayList<CriteriaObject.RestrictionValues> restrictionValues = new ArrayList<>();

        SearchUnit value = userRoleSearchDto.getValue();
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
