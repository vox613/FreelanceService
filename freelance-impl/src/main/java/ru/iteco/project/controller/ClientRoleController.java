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
import ru.iteco.project.resource.ClientRoleResource;
import ru.iteco.project.resource.dto.ClientRoleBaseDto;
import ru.iteco.project.resource.dto.ClientRoleDtoRequest;
import ru.iteco.project.resource.dto.ClientRoleDtoResponse;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.searching.ClientRoleSearchDto;
import ru.iteco.project.service.ClientRoleService;
import ru.iteco.project.validator.ClientRoleDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;
import static ru.iteco.project.controller.audit.AuditCode.*;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Client
 */
@RestController
public class ClientRoleController implements ClientRoleResource {

    /*** Объект сервисного слоя для ClientRole*/
    private final ClientRoleService clientRoleService;

    /*** Объект валидатора для ClientDtoRequest*/
    private final ClientRoleDtoRequestValidator clientRoleDtoRequestValidator;


    public ClientRoleController(ClientRoleService clientRoleService, ClientRoleDtoRequestValidator clientRoleDtoRequestValidator) {
        this.clientRoleService = clientRoleService;
        this.clientRoleDtoRequestValidator = clientRoleDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<ClientRoleDtoResponse>> getAllClientRole() {
        beforeCall(Level.DEBUG, "getAllClientRole()", "{}");
        List<ClientRoleDtoResponse> allClientsRoles = clientRoleService.getAllClientsRoles();
        afterCall(Level.DEBUG, "getAllClientRole()", allClientsRoles);
        return ResponseEntity.ok().body(allClientsRoles);
    }


    @Override
    public ResponseEntity<ClientRoleDtoResponse> getClientRole(UUID id) {
        beforeCall(Level.DEBUG, "getClientRole()", id);
        ClientRoleDtoResponse clientRoleById = clientRoleService.getClientRoleById(id);
        afterCall(Level.DEBUG, "getClientRole()", clientRoleById);
        if ((clientRoleById != null) && (clientRoleById.getId() != null)) {
            return ResponseEntity.ok().body(clientRoleById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getClients(ClientRoleSearchDto clientRoleSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getClients()", clientRoleSearchDto, pageable);
        PageDto roles = clientRoleService.getRoles(clientRoleSearchDto, pageable);
        afterCall(Level.DEBUG, "getClients()", roles);
        return roles;
    }


    @Override
    @Audit(operation = CLIENT_ROLE_CREATE)
    public ResponseEntity<? extends ClientRoleBaseDto> createClientRole(ClientRoleDtoRequest clientRoleDtoRequest,
                                                                        BindingResult result,
                                                                        UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createClientRole()", clientRoleDtoRequest);
        if (result.hasErrors()) {
            clientRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientRoleDtoRequest);
        }

        ClientRoleDtoResponse roleDtoResponse = clientRoleService.createClientRole(clientRoleDtoRequest);
        afterCall(Level.DEBUG, "createClientRole()", roleDtoResponse);

        if (roleDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("/roles/" + roleDtoResponse.getId()).buildAndExpand(roleDtoResponse).toUri();
            return ResponseEntity.created(uri).body(roleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    @Audit(operation = CLIENT_ROLE_UPDATE)
    public ResponseEntity<? extends ClientRoleBaseDto> updateClientRole(UUID id, ClientRoleDtoRequest clientRoleDtoRequest,
                                                                        BindingResult result) {
        beforeCall(Level.DEBUG, "updateClientRole()", id, clientRoleDtoRequest);
        if (result.hasErrors()) {
            clientRoleDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientRoleDtoRequest);
        }

        ClientRoleDtoResponse clientRoleDtoResponse = clientRoleService.updateClientRole(id, clientRoleDtoRequest);
        afterCall(Level.DEBUG, "updateClientRole()", clientRoleDtoResponse);

        if (clientRoleDtoResponse != null) {
            return ResponseEntity.ok().body(clientRoleDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(clientRoleDtoRequest);
        }
    }


    @Override
    @Audit(operation = CLIENT_ROLE_DELETE)
    public ResponseEntity<Object> deleteClientRole(UUID id) {
        beforeCall(Level.DEBUG, "deleteClientRole()", id);
        Boolean isDeleted = clientRoleService.deleteClientRole(id);
        afterCall(Level.DEBUG, "deleteClientRole()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "clientRoleDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(clientRoleDtoRequestValidator);
    }

}
