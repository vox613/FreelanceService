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
import ru.iteco.project.controller.dto.UserRoleBaseDto;
import ru.iteco.project.controller.dto.UserRoleDtoRequest;
import ru.iteco.project.controller.dto.UserRoleDtoResponse;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.controller.searching.UserRoleSearchDto;
import ru.iteco.project.service.UserRoleService;
import ru.iteco.project.validator.UserRoleDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с User
 */
@RestController
@RequestMapping(path = "/api/v1/roles/users")
public class UserRoleController {

    /*** Объект сервисного слоя для UserRole*/
    private final UserRoleService userRoleService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserRoleDtoRequestValidator userRoleDtoRequestValidator;


    public UserRoleController(UserRoleService userRoleService, UserRoleDtoRequestValidator userRoleDtoRequestValidator) {
        this.userRoleService = userRoleService;
        this.userRoleDtoRequestValidator = userRoleDtoRequestValidator;
    }

    /**
     * Контроллер возвращает список всех пользовательских ролей
     *
     * @return - список UserDtoResponse
     */
    @GetMapping
    ResponseEntity<List<UserRoleDtoResponse>> getAllUserRole() {
        ArrayList<UserRoleDtoResponse> allUsersRoles = userRoleService.getAllUsersRoles();
        return ResponseEntity.ok().body(allUsersRoles);
    }


    /**
     * Контроллер возвращает UserRoleDtoResponse роли с заданным id
     *
     * @param id - уникальный идентификатор роли пользователя
     * @return UserRoleDtoResponse заданной роли или пустой UserRoleDtoResponse, если данная роль не существует
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserRoleDtoResponse> getUserRole(@PathVariable UUID id) {
        UserRoleDtoResponse userRoleById = userRoleService.getUserRoleById(id);
        if ((userRoleById != null) && (userRoleById.getId() != null)) {
            return ResponseEntity.ok().body(userRoleById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param userRoleSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable          - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @GetMapping(path = "/search")
    public PageDto<UserRoleDtoResponse> getUsers(@RequestBody(required = false) UserRoleSearchDto userRoleSearchDto,
                                                 @PageableDefault(size = 5,
                                                         page = 0,
                                                         sort = {"createdAt"},
                                                         direction = Sort.Direction.ASC) Pageable pageable) {

        return userRoleService.getRoles(userRoleSearchDto, pageable);
    }


    /**
     * Создает новую роль пользователя
     *
     * @param userRoleDtoRequest - тело запроса на создание роли пользователя
     * @return Тело запроса на создание роли пользователя с уникальным проставленным id,
     * * или тело запроса с id = null, если создать роли не удалось
     */
    @PostMapping
    public ResponseEntity<? extends UserRoleBaseDto> createUserRole(@Validated @RequestBody UserRoleDtoRequest userRoleDtoRequest,
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


    /**
     * Обновляет существующую роли пользователя {id}
     *
     * @param id                 - уникальный идентификатор роли пользователя
     * @param userRoleDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends UserRoleBaseDto> updateUserRole(@PathVariable UUID id,
                                                                    @Validated @RequestBody UserRoleDtoRequest userRoleDtoRequest,
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


    /**
     * Удаляет роль пользователя с заданным id
     *
     * @param id - уникальный идентификатор роли для удаления
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable UUID id) {
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
