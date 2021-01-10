package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.resource.UserResource;
import ru.iteco.project.resource.dto.UserBaseDto;
import ru.iteco.project.resource.dto.UserDtoRequest;
import ru.iteco.project.resource.dto.UserDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.UserSearchDto;
import ru.iteco.project.service.UserService;
import ru.iteco.project.validator.UserDtoRequestValidator;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        List<UserDtoResponse> allUsers = userService.getAllUsers();
        return ResponseEntity.ok().body(allUsers);
    }

    @Override
    public PageDto getUsers(UserSearchDto userSearchDto, Pageable pageable) {
        return userService.getUsers(userSearchDto, pageable);
    }


    @Override
    public ResponseEntity<UserDtoResponse> getUser(UUID id) {
        UserDtoResponse userById = userService.getUserById(id);
        if ((userById != null) && (userById.getId() != null)) {
            return ResponseEntity.ok().body(userById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public ResponseEntity<? extends UserBaseDto> createUser(UserDtoRequest userDtoRequest, BindingResult result,
                                                            UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        UserDtoResponse userDtoResponse = userService.createUser(userDtoRequest);

        if (userDtoResponse != null) {
            URI uri = componentsBuilder.path("/users/" + userDtoResponse.getId()).buildAndExpand(userDtoResponse).toUri();
            return ResponseEntity.created(uri).body(userDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }

    }


    @Override
    public ResponseEntity<List<? extends Serializable>> createBatchUser(ArrayList<UserDtoRequest> userDtoRequestList,
                                                                        UriComponentsBuilder componentsBuilder,
                                                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().body(result.getAllErrors());
        }

        List<UserDtoResponse> bundleUsers = userService.createBundleUsers(userDtoRequestList);
        URI uri = componentsBuilder.path("/").build().toUri();
        return ResponseEntity.created(uri).body(bundleUsers);
    }


    @Override
    public ResponseEntity<? extends UserBaseDto> updateUser(UUID id, UserDtoRequest userDtoRequest, BindingResult result) {

        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        UserDtoResponse userDtoResponse = userService.updateUser(userDtoRequest);

        if (userDtoResponse != null) {
            return ResponseEntity.ok().body(userDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }
    }


    @Override
    public ResponseEntity<Object> deleteUser(UUID id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = {"userDtoRequest", "userDtoRequestList"})
    private void initUserDtoRequestBinder(WebDataBinder binder) {
        binder.setValidator(userDtoRequestValidator);
    }

}
