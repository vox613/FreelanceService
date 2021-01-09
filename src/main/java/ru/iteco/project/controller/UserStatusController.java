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
import ru.iteco.project.controller.dto.UserStatusBaseDto;
import ru.iteco.project.controller.dto.UserStatusDtoRequest;
import ru.iteco.project.controller.dto.UserStatusDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.UserStatusSearchDto;
import ru.iteco.project.service.UserStatusService;
import ru.iteco.project.validator.UserStatusDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с UserStatus
 */
@RestController
@RequestMapping(path = "/api/v1/statuses/users")
public class UserStatusController {

    /*** Объект сервисного слоя для UserRole*/
    private final UserStatusService userStatusService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserStatusDtoRequestValidator userStatusDtoRequestValidator;


    public UserStatusController(UserStatusService userStatusService, UserStatusDtoRequestValidator userStatusDtoRequestValidator) {
        this.userStatusService = userStatusService;
        this.userStatusDtoRequestValidator = userStatusDtoRequestValidator;
    }

    /**
     * Контроллер возвращает список всех пользовательских статусов
     *
     * @return - список UserStatusDtoResponse
     */
    @GetMapping
    ResponseEntity<List<UserStatusDtoResponse>> getAllUserStatus() {
        ArrayList<UserStatusDtoResponse> allUsersStatuses = userStatusService.getAllUsersStatuses();
        return ResponseEntity.ok().body(allUsersStatuses);
    }


    /**
     * Контроллер возвращает UserStatusDtoResponse статуса с заданным id
     *
     * @param id - уникальный идентификатор статуса пользователя
     * @return UserStatusDtoResponse заданного статуса или пустой UserStatusDtoResponse, если данный статус не существует
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserStatusDtoResponse> getUserStatus(@PathVariable UUID id) {
        UserStatusDtoResponse userStatusById = userStatusService.getUserStatusById(id);
        if ((userStatusById != null) && (userStatusById.getId() != null)) {
            return ResponseEntity.ok().body(userStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param userStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable            - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @GetMapping(path = "/search")
    public PageDto<UserStatusDtoResponse> getUsers(@RequestBody(required = false) UserStatusSearchDto userStatusSearchDto,
                                                   @PageableDefault(size = 5,
                                                           page = 0,
                                                           sort = {"createdAt"},
                                                           direction = Sort.Direction.ASC) Pageable pageable) {

        return userStatusService.getStatus(userStatusSearchDto, pageable);
    }


    /**
     * Создает новый статус пользователя
     *
     * @param userStatusDtoRequest - тело запроса на создание статуса пользователя
     * @return Тело запроса на создание статуса пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping
    public ResponseEntity<? extends UserStatusBaseDto> createUserStatus(@Validated @RequestBody UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse userStatusDtoResponse = userStatusService.createUserStatus(userStatusDtoRequest);

        if (userStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/statuses/users/" + userStatusDtoResponse.getId()).buildAndExpand(userStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(userStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    /**
     * Обновляет существующий статус пользователя {id}
     *
     * @param id                   - уникальный идентификатор статуса пользователя
     * @param userStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends UserStatusBaseDto> updateUserStatus(@PathVariable UUID id,
                                                                        @Validated @RequestBody UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result) {

        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse statusDtoResponse = userStatusService.updateUserStatus(id, userStatusDtoRequest);

        if (statusDtoResponse != null) {
            return ResponseEntity.ok().body(statusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    /**
     * Удаляет статус пользователя с заданным id
     *
     * @param id - уникальный идентификатор статуса для удаления
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteUserStatus(@PathVariable UUID id) {
        if (userStatusService.deleteUserStatus(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userStatusDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(userStatusDtoRequestValidator);
    }

}
