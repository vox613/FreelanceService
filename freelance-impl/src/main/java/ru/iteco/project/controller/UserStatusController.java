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
import ru.iteco.project.resource.UserStatusResource;
import ru.iteco.project.resource.dto.UserStatusBaseDto;
import ru.iteco.project.resource.dto.UserStatusDtoRequest;
import ru.iteco.project.resource.dto.UserStatusDtoResponse;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.resource.searching.UserStatusSearchDto;
import ru.iteco.project.service.UserStatusService;
import ru.iteco.project.validator.UserStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;
import static ru.iteco.project.controller.audit.AuditCode.*;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с UserStatus
 */
@RestController
public class UserStatusController implements UserStatusResource {

    /*** Объект сервисного слоя для UserRole*/
    private final UserStatusService userStatusService;

    /*** Объект валидатора для UserDtoRequest*/
    private final UserStatusDtoRequestValidator userStatusDtoRequestValidator;


    public UserStatusController(UserStatusService userStatusService, UserStatusDtoRequestValidator userStatusDtoRequestValidator) {
        this.userStatusService = userStatusService;
        this.userStatusDtoRequestValidator = userStatusDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<UserStatusDtoResponse>> getAllUserStatus() {
        beforeCall(Level.DEBUG, "getAllUserStatus()", "{}");
        List<UserStatusDtoResponse> allUsersStatuses = userStatusService.getAllUsersStatuses();
        afterCall(Level.DEBUG, "getAllUserStatus()", allUsersStatuses);
        return ResponseEntity.ok().body(allUsersStatuses);
    }


    @Override
    public ResponseEntity<UserStatusDtoResponse> getUserStatus(UUID id) {
        beforeCall(Level.DEBUG, "getUserStatus()", id);
        UserStatusDtoResponse userStatusById = userStatusService.getUserStatusById(id);
        afterCall(Level.DEBUG, "getUserStatus()", userStatusById);
        if ((userStatusById != null) && (userStatusById.getId() != null)) {
            return ResponseEntity.ok().body(userStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getUsers(UserStatusSearchDto userStatusSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getUsers()", userStatusSearchDto, pageable);
        PageDto status = userStatusService.getStatus(userStatusSearchDto, pageable);
        afterCall(Level.DEBUG, "getUsers()", status);
        return status;
    }


    @Override
    @Audit(operation = USER_STATUS_CREATE)
    public ResponseEntity<? extends UserStatusBaseDto> createUserStatus(UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createUserStatus()", userStatusDtoRequest);

        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse userStatusDtoResponse = userStatusService.createUserStatus(userStatusDtoRequest);
        afterCall(Level.DEBUG, "createUserStatus()", userStatusDtoResponse);

        if (userStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/statuses/users/" + userStatusDtoResponse.getId()).buildAndExpand(userStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(userStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @Override
    @Audit(operation = USER_STATUS_UPDATE)
    public ResponseEntity<? extends UserStatusBaseDto> updateUserStatus(UUID id, UserStatusDtoRequest userStatusDtoRequest,
                                                                        BindingResult result) {
        beforeCall(Level.DEBUG, "updateUserStatus()", id, userStatusDtoRequest);

        if (result.hasErrors()) {
            userStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(userStatusDtoRequest);
        }

        UserStatusDtoResponse statusDtoResponse = userStatusService.updateUserStatus(id, userStatusDtoRequest);
        afterCall(Level.DEBUG, "updateUserStatus()", statusDtoResponse);

        if (statusDtoResponse != null) {
            return ResponseEntity.ok().body(statusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }

    @Override
    @Audit(operation = USER_STATUS_DELETE)
    public ResponseEntity<Object> deleteUserStatus(UUID id) {
        beforeCall(Level.DEBUG, "deleteUserStatus()", id);
        Boolean isDeleted = userStatusService.deleteUserStatus(id);
        afterCall(Level.DEBUG, "deleteUserStatus()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "userStatusDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(userStatusDtoRequestValidator);
    }

}
