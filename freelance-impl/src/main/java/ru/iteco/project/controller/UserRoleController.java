package ru.iteco.project.controller;

import org.apache.logging.log4j.Level;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.enumaration.AuditCode;
import ru.iteco.project.resource.UserRoleResource;
import ru.iteco.project.resource.dto.UserRoleBaseDto;
import ru.iteco.project.resource.dto.UserRoleDtoRequest;
import ru.iteco.project.resource.dto.UserRoleDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.UserRoleSearchDto;
import ru.iteco.project.service.UserRoleService;
import ru.iteco.project.validator.UserRoleDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
public class UserRoleController implements UserRoleResource {

    /*** Объект сервисного слоя для UserRole*/
    private final UserRoleService userRoleService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserRoleDtoRequestValidator userRoleDtoRequestValidator;


    public UserRoleController(UserRoleService userRoleService, UserRoleDtoRequestValidator userRoleDtoRequestValidator) {
        this.userRoleService = userRoleService;
        this.userRoleDtoRequestValidator = userRoleDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<UserRoleDtoResponse>> getAllUserRole() {
        beforeCall(Level.DEBUG, "getAllUserRole()", "{}");
        List<UserRoleDtoResponse> allUsersRoles = userRoleService.getAllUsersRoles();
        afterCall(Level.DEBUG, "getAllUserRole()", allUsersRoles);
        return ResponseEntity.ok().body(allUsersRoles);
    }


    @Override
    public ResponseEntity<UserRoleDtoResponse> getUserRole(UUID id) {
        beforeCall(Level.DEBUG, "getUserRole()", id);
        UserRoleDtoResponse userRoleById = userRoleService.getUserRoleById(id);
        afterCall(Level.DEBUG, "getUserRole()", userRoleById);
        if ((userRoleById != null) && (userRoleById.getId() != null)) {
            return ResponseEntity.ok().body(userRoleById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getUsers(UserRoleSearchDto userRoleSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getUsers()", userRoleSearchDto, pageable);
        PageDto roles = userRoleService.getRoles(userRoleSearchDto, pageable);
        afterCall(Level.DEBUG, "getUsers()", roles);
        return roles;
    }


    @Override
    @Audit(operation = AuditCode.USER_ROLE_CREATE)
    public ResponseEntity<? extends UserRoleBaseDto> createUserRole(UserRoleDtoRequest userRoleDtoRequest,
                                                                    BindingResult result,
                                                                    UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createUserRole()", userRoleDtoRequest);
        if (result.hasErrors()) {
            userRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }

        UserRoleDtoResponse roleDtoResponse = userRoleService.createUserRole(userRoleDtoRequest);
        afterCall(Level.DEBUG, "createUserRole()", roleDtoResponse);

        if (roleDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/roles/" + roleDtoResponse.getId()).buildAndExpand(roleDtoResponse).toUri();
            return ResponseEntity.created(uri).body(roleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    @Audit(operation = AuditCode.USER_ROLE_UPDATE)
    public ResponseEntity<? extends UserRoleBaseDto> updateUserRole(UUID id, UserRoleDtoRequest userRoleDtoRequest,
                                                                    BindingResult result) {
        beforeCall(Level.DEBUG, "updateUserRole()", id, userRoleDtoRequest);
        if (result.hasErrors()) {
            userRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }

        UserRoleDtoResponse userRoleDtoResponse = userRoleService.updateUserRole(id, userRoleDtoRequest);
        afterCall(Level.DEBUG, "updateUserRole()", userRoleDtoResponse);

        if (userRoleDtoResponse != null) {
            return ResponseEntity.ok().body(userRoleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }
    }


    @Override
    @Audit(operation = AuditCode.USER_ROLE_DELETE)
    public ResponseEntity<Object> deleteUser(UUID id) {
        beforeCall(Level.DEBUG, "deleteUser()", id);
        Boolean isDeleted = userRoleService.deleteUserRole(id);
        afterCall(Level.DEBUG, "deleteUser()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userRoleDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(userRoleDtoRequestValidator);
    }

}
