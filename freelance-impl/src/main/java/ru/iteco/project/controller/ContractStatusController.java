package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
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
        List<ContractStatusDtoResponse> allContractStatuses = contractStatusService.getAllContractsStatuses();
        return ResponseEntity.ok().body(allContractStatuses);
    }


    @Override
    public ResponseEntity<ContractStatusDtoResponse> getContractStatus(UUID id) {
        ContractStatusDtoResponse contractStatusById = contractStatusService.getContractStatusById(id);
        if ((contractStatusById != null) && (contractStatusById.getId() != null)) {
            return ResponseEntity.ok().body(contractStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getContracts(ContractStatusSearchDto contractStatusSearchDto, Pageable pageable) {
        return contractStatusService.getStatus(contractStatusSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends ContractStatusBaseDto> createContractStatus(ContractStatusDtoRequest contractStatusDtoRequest,
                                                                                BindingResult result,
                                                                                UriComponentsBuilder componentsBuilder) {
        if (result.hasErrors()) {
            contractStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractStatusDtoRequest);
        }

        ContractStatusDtoResponse contractStatusDtoResponse = contractStatusService.createContractStatus(contractStatusDtoRequest);

        if (contractStatusDtoResponse.getId() != null) {
            URI uri = componentsBuilder.path("statuses/contracts/" + contractStatusDtoResponse.getId()).buildAndExpand(contractStatusDtoResponse).toUri();
            return ResponseEntity.created(uri).body(contractStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    public ResponseEntity<? extends ContractStatusBaseDto> updateTaskStatus(UUID id, ContractStatusDtoRequest contractStatusDtoRequest,
                                                                            BindingResult result) {

        if (result.hasErrors()) {
            contractStatusDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractStatusDtoRequest);
        }

        ContractStatusDtoResponse contractStatusDtoResponse = contractStatusService.updateContractStatus(id, contractStatusDtoRequest);

        if (contractStatusDtoResponse != null) {
            return ResponseEntity.ok().body(contractStatusDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }


    @Override
    public ResponseEntity<Object> deleteTaskStatus(UUID id) {
        if (contractStatusService.deleteContractStatus(id)) {
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
