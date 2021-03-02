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
import ru.iteco.project.resource.ClientResource;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.dto.ClientBaseDto;
import ru.iteco.project.resource.dto.ClientDtoRequest;
import ru.iteco.project.resource.dto.ClientDtoResponse;
import ru.iteco.project.resource.searching.ClientSearchDto;
import ru.iteco.project.service.ClientService;
import ru.iteco.project.validator.ClientDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.controller.audit.AuditCode.*;
import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Client
 */
@RestController
public class ClientController implements ClientResource {

    /*** Объект сервисного слоя для Client*/
    private final ClientService clientService;

    /*** Объект валидатора для ClientDtoRequest*/
    private final ClientDtoRequestValidator clientDtoRequestValidator;


    public ClientController(ClientService clientService, ClientDtoRequestValidator clientDtoRequestValidator) {
        this.clientService = clientService;
        this.clientDtoRequestValidator = clientDtoRequestValidator;
    }


    @Override
    public ResponseEntity<List<ClientDtoResponse>> getAllClients() {
        beforeCall(Level.DEBUG, "getAllClients()", "{}");
        List<ClientDtoResponse> allClients = clientService.getAllClients();
        afterCall(Level.DEBUG, "getAllClients()", allClients);
        return ResponseEntity.ok().body(allClients);
    }

    @Override
    public PageDto getClients(ClientSearchDto clientSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getClients()", clientSearchDto, pageable);
        PageDto clients = clientService.getClients(clientSearchDto, pageable);
        afterCall(Level.DEBUG, "getClients()", clients);
        return clients;
    }


    @Override
    public ResponseEntity<ClientDtoResponse> getClient(UUID id) {
        beforeCall(Level.DEBUG, "getClient()", id);
        ClientDtoResponse clientById = clientService.getClientById(id);
        afterCall(Level.DEBUG, "getClient()", clientById);
        if ((clientById != null) && (clientById.getId() != null)) {
            return ResponseEntity.ok().body(clientById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @Audit(operation = CLIENT_CREATE)
    public ResponseEntity<? extends ClientBaseDto> createClient(ClientDtoRequest clientDtoRequest, BindingResult result,
                                                                UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createClient()", clientDtoRequest);

        if (result.hasErrors()) {
            clientDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientDtoRequest);
        }

        ClientDtoResponse clientDtoResponse = clientService.createClient(clientDtoRequest);
        afterCall(Level.DEBUG, "createClient()", clientDtoResponse);

        if (clientDtoResponse != null) {
            URI uri = componentsBuilder.path("/clients/" + clientDtoResponse.getId()).buildAndExpand(clientDtoResponse).toUri();
            return ResponseEntity.created(uri).body(clientDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }

    }


    @Override
    @Audit(operation = CLIENT_UPDATE)
    public ResponseEntity<? extends ClientBaseDto> updateClient(UUID id, ClientDtoRequest clientDtoRequest, BindingResult result) {

        beforeCall(Level.DEBUG, "updateClient()", id, clientDtoRequest);

        if (result.hasErrors()) {
            clientDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(clientDtoRequest);
        }

        ClientDtoResponse clientDtoResponse = clientService.updateClient(clientDtoRequest);
        afterCall(Level.DEBUG, "updateClient()", clientDtoResponse);

        if (clientDtoResponse != null) {
            return ResponseEntity.ok().body(clientDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(clientDtoRequest);
        }
    }


    @Override
    @Audit(operation = CLIENT_DELETE)
    public ResponseEntity<Object> deleteClient(UUID id) {
        beforeCall(Level.DEBUG, "deleteClient()", id);
        Boolean isDeleted = clientService.deleteClient(id);
        afterCall(Level.DEBUG, "deleteClient()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = {"clientDtoRequest", "clientDtoRequestList"})
    public void initClientDtoRequestBinder(WebDataBinder binder) {
        binder.setValidator(clientDtoRequestValidator);
    }

}
