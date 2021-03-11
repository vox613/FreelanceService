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
import ru.iteco.project.resource.ContractResource;
import ru.iteco.project.resource.PageDto;
import ru.iteco.project.resource.dto.ContractBaseDto;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoResponse;
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.service.ContractService;
import ru.iteco.project.validator.ContractDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.controller.audit.AuditCode.*;
import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с Contract
 */
@RestController
public class ContractController implements ContractResource {

    /*** Объект сервисного слоя для Contract*/
    private final ContractService contractService;

    /*** Объект валидатора для ContractDtoRequest*/
    private final ContractDtoRequestValidator contractDtoRequestValidator;


    public ContractController(ContractService contractService, ContractDtoRequestValidator contractDtoRequestValidator) {
        this.contractService = contractService;
        this.contractDtoRequestValidator = contractDtoRequestValidator;
    }


    @Override
    public ResponseEntity<List<ContractDtoResponse>> getAllContracts() {
        beforeCall(Level.DEBUG, "getAllContracts()", "{}");
        List<ContractDtoResponse> allContracts = contractService.getAllContracts();
        afterCall(Level.DEBUG, "getAllContracts()", allContracts);
        return ResponseEntity.ok().body(allContracts);
    }


    @Override
    public ResponseEntity<ContractDtoResponse> getContract(UUID id) {
        beforeCall(Level.DEBUG, "getContract()", id);
        ContractDtoResponse contractById = contractService.getContractById(id);
        afterCall(Level.DEBUG, "getContract()", contractById);
        return ResponseEntity.ok().body(contractById);
    }


    @Override
    public PageDto getContracts(ContractSearchDto contractSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getContracts()", contractSearchDto, pageable);
        PageDto contracts = contractService.getContracts(contractSearchDto, pageable);
        afterCall(Level.DEBUG, "getContracts()", contracts);
        return contracts;
    }


    @Override
    @Audit(operation = CONTRACT_CREATE)
    public ResponseEntity<? extends ContractBaseDto> createContract(ContractDtoRequest contractDtoRequest,
                                                                    UriComponentsBuilder componentsBuilder,
                                                                    BindingResult result) {

        beforeCall(Level.DEBUG, "createContract()", contractDtoRequest);
        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.createContract(contractDtoRequest);
        afterCall(Level.DEBUG, "createContract()", contractDtoResponse);

        URI uri = componentsBuilder
                .path(String.format("/api/v1/contracts/%s", contractDtoResponse.getId()))
                .buildAndExpand(contractDtoResponse)
                .toUri();

        return ResponseEntity.created(uri).body(contractDtoResponse);
    }


    @Override
    @Audit(operation = CONTRACT_UPDATE)
    public ResponseEntity<? extends ContractBaseDto> updateContract(ContractDtoRequest contractDtoRequest, UUID id,
                                                                    BindingResult result) {

        beforeCall(Level.DEBUG, "updateContract()", contractDtoRequest, id);
        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.updateContract(contractDtoRequest);
        afterCall(Level.DEBUG, "updateContract()", contractDtoResponse);
        return ResponseEntity.ok().body(contractDtoResponse);
    }


    @Override
    @Audit(operation = CONTRACT_DELETE)
    public ResponseEntity<ContractDtoResponse> deleteContract(UUID id) {
        beforeCall(Level.DEBUG, "deleteContract()", id);
        Boolean isDeleted = contractService.deleteContract(id);
        afterCall(Level.DEBUG, "deleteContract()", isDeleted);
        return ResponseEntity.ok().build();
    }


    @InitBinder(value = "contractDtoRequest")
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(contractDtoRequestValidator);
    }

}
