package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.controller.dto.UserBaseDto;
import ru.iteco.project.controller.dto.UserDtoRequest;
import ru.iteco.project.controller.dto.UserDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.UserSearchDto;
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
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param userSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable      - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @GetMapping(path = "/search")
    public PageDto<UserDtoResponse> getUsers(@RequestBody(required = false) UserSearchDto userSearchDto,
                                             @PageableDefault(size = 5,
                                                     page = 0,
                                                     sort = {"createdAt"},
                                                     direction = Sort.Direction.DESC) Pageable pageable) {

        return userService.getUsers(userSearchDto, pageable);
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
     *
     * @param userDtoRequestList - список пользователей для добавления
     * @param componentsBuilder  - билдер для формирования url ресура
     * @return - список созданных пользователей в представлении UserDtoResponse
     */
    @PostMapping(value = "/batch")
    public ResponseEntity<List<? extends Serializable>> createBatchUser(@Validated @RequestBody ArrayList<UserDtoRequest> userDtoRequestList,
                                                                        UriComponentsBuilder componentsBuilder,
                                                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().body(result.getAllErrors());
        }

        List<UserDtoResponse> bundleUsers = userService.createBundleUsers(userDtoRequestList);
        URI uri = componentsBuilder.path("/").build().toUri();
        return ResponseEntity.created(uri).body(bundleUsers);
    }


    /**
     * Обновляет существующего пользователя {id}
     *
     * @param userDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends UserBaseDto> updateUser(@Validated @RequestBody UserDtoRequest userDtoRequest,
                                                            BindingResult result) {

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

    @InitBinder(value = {"userDtoRequest", "userDtoRequestList"})
    private void initUserDtoRequestBinder(WebDataBinder binder) {
        binder.setValidator(userDtoRequestValidator);
    }

}
