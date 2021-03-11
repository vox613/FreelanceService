package ru.iteco.project.controller;

import org.apache.logging.log4j.Level;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.annotation.Audit;
import ru.iteco.project.config.security.TokenAuthentication;
import ru.iteco.project.config.security.UserPrincipal;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.UserResource;
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.resource.dto.UserInfoDTO;
import ru.iteco.project.resource.searching.UserSearchDto;
import ru.iteco.project.service.UserService;
import ru.iteco.project.validator.UserDtoRequestValidator;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.controller.audit.AuditCode.*;
import static ru.iteco.project.utils.LoggerUtils.afterCall;
import static ru.iteco.project.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
public class UserController implements UserResource {

    /*** Объект сервисного слоя для User*/
    private final UserService userService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserDtoRequestValidator userDtoRequestValidator;


    public UserController(UserService userService, UserDtoRequestValidator userDtoRequestValidator) {
        this.userService = userService;
        this.userDtoRequestValidator = userDtoRequestValidator;
    }


    @Override
    public ResponseEntity<List<UserDtoResponse>> getAllUsers() {
        beforeCall(Level.DEBUG, "getAllUsers()", "{}");
        List<UserDtoResponse> allUsers = userService.getAllUsers();
        afterCall(Level.DEBUG, "getAllUsers()", allUsers);
        return ResponseEntity.ok().body(allUsers);
    }

    @Override
    public PageDto getUsers(UserSearchDto userSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getUsers()", userSearchDto, pageable);
        PageDto users = userService.getUsers(userSearchDto, pageable);
        afterCall(Level.DEBUG, "getUsers()", users);
        return users;
    }


    @Override
    public ResponseEntity<UserDtoResponse> getUser(UUID id) {
        beforeCall(Level.DEBUG, "getUser()", id);
        UserDtoResponse userById = userService.getUserById(id);
        afterCall(Level.DEBUG, "getUser()", userById);
        return ResponseEntity.ok().body(userById);
    }

    @Override
    @Audit(operation = USER_CREATE)
    public ResponseEntity<? extends UserBaseDto> createUser(UserDtoRequest userDtoRequest, BindingResult result,
                                                            UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createUser()", userDtoRequest);

        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        UserDtoResponse userDtoResponse = userService.createUser(userDtoRequest);
        afterCall(Level.DEBUG, "createUser()", userDtoResponse);
        URI uri = componentsBuilder.path("/api/v1/users/" + userDtoResponse.getId()).buildAndExpand(userDtoResponse).toUri();
        return ResponseEntity.created(uri).body(userDtoResponse);
    }


    @Override
    @Audit(operation = USER_BATCH_CREATE)
    public ResponseEntity<List<? extends Serializable>> createBatchUser(ArrayList<UserDtoRequest> userDtoRequestList,
                                                                        UriComponentsBuilder componentsBuilder,
                                                                        BindingResult result) {
        beforeCall(Level.DEBUG, "createBatchUser()", userDtoRequestList);


        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().body(result.getAllErrors());
        }

        List<UserDtoResponse> bundleUsers = userService.createBundleUsers(userDtoRequestList);
        afterCall(Level.DEBUG, "createBatchUser()", bundleUsers);


        URI uri = componentsBuilder.path("/api/v1/users").build().toUri();
        return ResponseEntity.created(uri).body(bundleUsers);
    }


    @Override
    @Audit(operation = USER_UPDATE)
    public ResponseEntity<? extends UserBaseDto> updateUser(UUID id, UserDtoRequest userDtoRequest, BindingResult result) {

        beforeCall(Level.DEBUG, "updateUser()", id, userDtoRequest);

        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }
        UserDtoResponse userDtoResponse = userService.updateUser(userDtoRequest);
        afterCall(Level.DEBUG, "updateUser()", userDtoResponse);
        return ResponseEntity.ok().body(userDtoResponse);
    }


    @Override
    @Audit(operation = USER_DELETE)
    public ResponseEntity<Object> deleteUser(UUID id) {
        beforeCall(Level.DEBUG, "deleteUser()", id);
        Boolean isDeleted = userService.deleteUser(id);
        afterCall(Level.DEBUG, "deleteUser()", isDeleted);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserInfoDTO> getUserInfo() {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) (SecurityContextHolder.getContext().getAuthentication());
        UserPrincipal principal = (UserPrincipal) tokenAuthentication.getPrincipal();
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .setUsername(principal.getUsername())
                .setAccountNonExpired(principal.isAccountNonExpired())
                .setAccountNonLocked(principal.isAccountNonLocked())
                .setCredentialsNonExpired(principal.isCredentialsNonExpired())
                .setEnabled(principal.isEnabled())
                .setAuthenticated(tokenAuthentication.isAuthenticated())
                .setAuthorities(Arrays.asList(principal.getAuthorities().toArray())).build();
        return ResponseEntity.ok(userInfoDTO);
    }


    @InitBinder(value = {"userDtoRequest", "userDtoRequestList"})
    public void initUserDtoRequestBinder(WebDataBinder binder) {
        binder.setValidator(userDtoRequestValidator);
    }

}
