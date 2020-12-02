package ru.iteco.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.UserBaseDto;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.service.UserService;
import ru.iteco.project.validator.UserDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {

    /*** Объект сервисного слоя для User*/
    private final UserService userService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserDtoRequestValidator userDtoRequestValidator;


    public UserController(UserService userService, UserDtoRequestValidator userDtoRequestValidator) {
        this.userService = userService;
        this.userDtoRequestValidator = userDtoRequestValidator;
    }


    /**
     * Контроллер возвращает список всех созданных пользователей
     *
     * @return - список UserDtoResponse
     */
    @GetMapping
    ResponseEntity<List<UserDtoResponse>> getAllUsers() {
        ArrayList<UserDtoResponse> allUsers = userService.getAllUsers();
        return ResponseEntity.ok().body(allUsers);
    }


    /**
     * Контроллер возвращает UserDtoResponse пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя
     * @return ContractDtoResponse заданного пользователя или пустой ContractDtoResponse, если данный пользователь не существует
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDtoResponse> getUser(@PathVariable UUID id) {
        UserDtoResponse userById = userService.getUserById(id);
        if ((userById != null) && (userById.getId() != null)) {
            return ResponseEntity.ok().body(userById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Создает нового пользователя
     *
     * @param userDtoRequest - тело запроса на создание пользователя
     * @return Тело запроса на создание пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать пользователя не удалось
     */
    @PostMapping
    public ResponseEntity<? extends UserBaseDto> createUser(@Validated @RequestBody UserDtoRequest userDtoRequest,
                                                            BindingResult result,
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


    /**
     * Метод пакетного добаввления пользователей
     * @param userDtoRequestList - список пользователей для добавления
     * @param componentsBuilder - билдер для формирования url ресура
     * @return - список созданных пользователей в представлении UserDtoResponse
     */
    @PostMapping(value = "/batch")
    public ResponseEntity<List<UserDtoResponse>> createBatchUser(@RequestBody ArrayList<UserDtoRequest> userDtoRequestList,
                                                                 UriComponentsBuilder componentsBuilder) {

        List<UserDtoResponse> bundleUsers = userService.createBundleUsers(userDtoRequestList);
        URI uri = componentsBuilder.path("/").build().toUri();
        return ResponseEntity.created(uri).body(bundleUsers);
    }


    /**
     * Обновляет существующего пользователя {id}
     *
     * @param id             - уникальный идентификатор пользователя
     * @param userDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends UserBaseDto> updateUser(@PathVariable UUID id,
                                                            @Validated @RequestBody UserDtoRequest userDtoRequest,
                                                            BindingResult result) {

        if (result.hasErrors()) {
            userDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }

        UserDtoResponse userDtoResponse = userService.updateUser(id, userDtoRequest);

        if (userDtoResponse != null) {
            return ResponseEntity.ok().body(userDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(userDtoRequest);
        }
    }


    /**
     * Удаляет пользователя с заданным id
     *
     * @param id - уникальный идентификатор пользователя для удаления
     * @return - статус 200 если пользователь успешно удален и 404 если такого пользователя нет
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userDtoRequestValidator);
    }

}
