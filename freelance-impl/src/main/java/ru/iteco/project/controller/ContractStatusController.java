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
import ru.iteco.project.enumaration.AuditCode;
import ru.iteco.project.resource.ContractStatusResource;
import ru.iteco.project.resource.dto.ContractStatusBaseDto;
import ru.iteco.project.resource.dto.ContractStatusDtoRequest;
import ru.iteco.project.resource.dto.ContractStatusDtoResponse;
import ru.iteco.project.resource.searching.ContractStatusSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.service.ContractStatusService;
import ru.iteco.project.validator.ContractStatusDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static ru.iteco.project.logger.utils.LoggerUtils.afterCall;
import static ru.iteco.project.logger.utils.LoggerUtils.beforeCall;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с ContractStatus
 */
@RestController
public class ContractStatusController implements ContractStatusResource {

    /*** Объект сервисного слоя для ContractStatus*/
    private final ContractStatusService contractStatusService;

    /*** Объект валидатора для ContractStatusDtoRequest*/
    private final ContractStatusDtoRequestValidator contractStatusDtoRequestValidator;


    public ContractStatusController(ContractStatusService contractStatusService, ContractStatusDtoRequestValidator contractStatusDtoRequestValidator) {
        this.contractStatusService = contractStatusService;
        this.contractStatusDtoRequestValidator = contractStatusDtoRequestValidator;
    }

    @Override
    public ResponseEntity<List<ContractStatusDtoResponse>> getAllContractStatus() {
        beforeCall(Level.DEBUG, "getAllContractStatus()", "{}");
        List<ContractStatusDtoResponse> allContractStatuses = contractStatusService.getAllContractsStatuses();
        afterCall(Level.DEBUG, "getAllContractStatus()", allContractStatuses);
        return ResponseEntity.ok().body(allContractStatuses);
    }


    @Override
    public ResponseEntity<ContractStatusDtoResponse> getContractStatus(UUID id) {
        beforeCall(Level.DEBUG, "getContractStatus()", id);
        ContractStatusDtoResponse contractStatusById = contractStatusService.getContractStatusById(id);
        afterCall(Level.DEBUG, "getContractStatus()", contractStatusById);
        if ((contractStatusById != null) && (contractStatusById.getId() != null)) {
            return ResponseEntity.ok().body(contractStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getContracts(ContractStatusSearchDto contractStatusSearchDto, Pageable pageable) {
        beforeCall(Level.DEBUG, "getContracts()", contractStatusSearchDto, pageable);
        PageDto contractStatus = contractStatusService.getStatus(contractStatusSearchDto, pageable);
        afterCall(Level.DEBUG, "getContracts()", contractStatus);
        return contractStatus;
    }


    @Override
    @Audit(operation = AuditCode.CONTRACT_STATUS_CREATE)
    public ResponseEntity<? extends ContractStatusBaseDto> createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest,
                                                                                BindingResult result,
                                                                                UriComponentsBuilder componentsBuilder) {
        beforeCall(Level.DEBUG, "createContractStatus()", contractStatusDtoRequest);
        if (result.hasErrors()) {
            contractStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractStatusDtoRequest);
        }

        ContractStatusDtoResponse contractStatusDtoResponse = contractStatusService.createContractStatus(contractStatusDtoRequest);
        afterCall(Level.DEBUG, "createContractStatus()", contractStatusDtoResponse);

        if (contractStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("statuses/contracts/" + contractStatusDtoResponse.getId()).buildAndExpand(contractStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(contractStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    @Audit(operation = AuditCode.CONTRACT_STATUS_UPDATE)
    public ResponseEntity<? extends ContractStatusBaseDto> updateTaskStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest,
                                                                            BindingResult result) {
        beforeCall(Level.DEBUG, "updateTaskStatus()", id, contractStatusDtoRequest);
        if (result.hasErrors()) {
            contractStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractStatusDtoRequest);
        }

        ContractStatusDtoResponse contractStatusDtoResponse = contractStatusService.updateContractStatus(id, contractStatusDtoRequest);
        afterCall(Level.DEBUG, "updateTaskStatus()", contractStatusDtoResponse);
        if (contractStatusDtoResponse != null) {
            return ResponseEntity.ok().body(contractStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    @Override
    @Audit(operation = AuditCode.CONTRACT_STATUS_DELETE)
    public ResponseEntity<Object> deleteTaskStatus(UUID id) {
        beforeCall(Level.DEBUG, "deleteTaskStatus()", id);
        Boolean isDeleted = contractStatusService.deleteContractStatus(id);
        afterCall(Level.DEBUG, "deleteTaskStatus()", isDeleted);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @InitBinder(value = "contractStatusDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(contractStatusDtoRequestValidator);
    }

}
