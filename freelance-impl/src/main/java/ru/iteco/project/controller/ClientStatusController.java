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
import ru.iteco.project.resource.ClientStatusResource;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.dto.ClientStatusBaseDto;
import ru.iteco.project.resource.dto.ClientStatusDtoRequest;
import ru.iteco.project.resource.dto.ClientStatusDtoResponse;
import ru.iteco.project.resource.searching.ClientStatusSearchDto;
import ru.iteco.project.service.ClientStatusService;
import ru.iteco.project.validator.ClientStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.controller.audit.AuditCode.*;
import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с ClientStatus
 */
@RestController
public class ClientStatusController implements ClientStatusResource {

    /*** Объект сервисного слоя для ClientRole*/
    private final ClientStatusService clientStatusService;

    /*** Объект валидатора для ClientDtoRequest*/
    private final ClientStatusDtoRequestValidator clientStatusDtoRequestValidator;


    public ClientStatusController(ClientStatusService clientStatusService, ClientStatusDtoRequestValidator clientStatusDtoRequestValidator) {
        this.clientStatusService = clientStatusService;
        this.clientStatusDtoRequestValidator = clientStatusDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<ClientStatusDtoResponse>> getAllClientStatus() {
        beforeCall(Level.DEBUG, "getAllClientStatus()", "{}");
        List<ClientStatusDtoResponse> allClientsStatuses = clientStatusService.getAllClientStatuses();
        afterCall(Level.DEBUG, "getAllClientStatus()", allClientsStatuses);
        return ResponseEntity.ok().body(allClientsStatuses);
    }


    @Override
    public ResponseEntity<ClientStatusDtoResponse> getClientStatus(UUID id) {
        beforeCall(Level.DEBUG, "getClientStatus()", id);
        ClientStatusDtoResponse clientStatusById = clientStatusService.getClientStatusById(id);
        afterCall(Level.DEBUG, "getClientStatus()", clientStatusById);
        return ResponseEntity.ok().body(clientStatusById);
    }


    @Override
    public PageDto getClients(ClientStatusSearchDto clientStatusSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getClients()", clientStatusSearchDto, pageable);
        PageDto status = clientStatusService.getStatus(clientStatusSearchDto, pageable);
        afterCall(Level.DEBUG, "getClients()", status);
        return status;
    }


    @Override
    @Audit(operation = CLIENT_STATUS_CREATE)
    public ResponseEntity<? extends ClientStatusBaseDto> createClientStatus(ClientStatusDtoRequest clientStatusDtoRequest,
                                                                            BindingResult result,
                                                                            UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createClientStatus()", clientStatusDtoRequest);

        if (result.hasErrors()) {
            clientStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientStatusDtoRequest);
        }

        ClientStatusDtoResponse clientStatusDtoResponse = clientStatusService.createClientStatus(clientStatusDtoRequest);
        afterCall(Level.DEBUG, "createClientStatus()", clientStatusDtoResponse);
        URI uri = componentsBuilder.path("/api/v1/statuses/clients/" + clientStatusDtoResponse.getId()).buildAndExpand(clientStatusDtoResponse).toUri();
        return ResponseEntity.created(uri).body(clientStatusDtoResponse);
    }

    @Override
    @Audit(operation = CLIENT_STATUS_UPDATE)
    public ResponseEntity<? extends ClientStatusBaseDto> updateClientStatus(UUID id, ClientStatusDtoRequest clientStatusDtoRequest,
                                                                            BindingResult result) {
        beforeCall(Level.DEBUG, "updateClientStatus()", id, clientStatusDtoRequest);

        if (result.hasErrors()) {
            clientStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientStatusDtoRequest);
        }

        ClientStatusDtoResponse statusDtoResponse = clientStatusService.updateClientStatus(id, clientStatusDtoRequest);
        afterCall(Level.DEBUG, "updateClientStatus()", statusDtoResponse);
        return ResponseEntity.ok().body(statusDtoResponse);
    }

    @Override
    @Audit(operation = CLIENT_STATUS_DELETE)
    public ResponseEntity<Object> deleteClientStatus(UUID id) {
        beforeCall(Level.DEBUG, "deleteClientStatus()", id);
        Boolean isDeleted = clientStatusService.deleteClientStatus(id);
        afterCall(Level.DEBUG, "deleteClientStatus()", isDeleted);
        return ResponseEntity.ok().build();
    }

    @InitBinder(value = "clientStatusDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(clientStatusDtoRequestValidator);
    }

}
