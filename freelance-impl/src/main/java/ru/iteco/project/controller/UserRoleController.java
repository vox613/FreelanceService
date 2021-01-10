package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
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
        List<UserRoleDtoResponse> allUsersRoles = userRoleService.getAllUsersRoles();
        return ResponseEntity.ok().body(allUsersRoles);
    }


    @Override
    public ResponseEntity<UserRoleDtoResponse> getUserRole(UUID id) {
        UserRoleDtoResponse userRoleById = userRoleService.getUserRoleById(id);
        if ((userRoleById != null) && (userRoleById.getId() != null)) {
            return ResponseEntity.ok().body(userRoleById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getUsers(UserRoleSearchDto userRoleSearchDto, Pageable pageable) {
        return userRoleService.getRoles(userRoleSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends UserRoleBaseDto> createUserRole(UserRoleDtoRequest userRoleDtoRequest,
                                                                    BindingResult result,
                                                                    UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            userRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }

        UserRoleDtoResponse roleDtoResponse = userRoleService.createUserRole(userRoleDtoRequest);

        if (roleDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/roles/" + roleDtoResponse.getId()).buildAndExpand(roleDtoResponse).toUri();
            return ResponseEntity.created(uri).body(roleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    public ResponseEntity<? extends UserRoleBaseDto> updateUserRole(UUID id, UserRoleDtoRequest userRoleDtoRequest,
                                                                    BindingResult result) {

        if (result.hasErrors()) {
            userRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }

        UserRoleDtoResponse userRoleDtoResponse = userRoleService.updateUserRole(id, userRoleDtoRequest);

        if (userRoleDtoResponse != null) {
            return ResponseEntity.ok().body(userRoleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(userRoleDtoRequest);
        }
    }


    @Override
    public ResponseEntity<Object> deleteUser(UUID id) {
        if (userRoleService.deleteUserRole(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userRoleDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userRoleDtoRequestValidator);
    }

}
