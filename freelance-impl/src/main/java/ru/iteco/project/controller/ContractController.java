package ru.iteco.project.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.iteco.project.resource.ContractResource;
import ru.iteco.project.resource.dto.ContractBaseDto;
import ru.iteco.project.resource.dto.ContractDtoRequest;
import ru.iteco.project.resource.dto.ContractDtoResponse;
import ru.iteco.project.resource.searching.ContractSearchDto;
import ru.iteco.project.resource.searching.PageDto;
import ru.iteco.project.service.ContractService;
import ru.iteco.project.validator.ContractDtoRequestValidator;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
        List<ContractDtoResponse> allContracts = contractService.getAllContracts();
        return ResponseEntity.ok().body(allContracts);
    }


    @Override
    public ResponseEntity<ContractDtoResponse> getContract(UUID id) {
        ContractDtoResponse contractById = contractService.getContractById(id);
        if ((contractById != null) && (contractById.getId() != null)) {
            return ResponseEntity.ok().body(contractById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Override
    public PageDto getContracts(ContractSearchDto contractSearchDto, Pageable pageable) {
        return contractService.getContracts(contractSearchDto, pageable);
    }


    @Override
    public ResponseEntity<? extends ContractBaseDto> createContract(ContractDtoRequest contractDtoRequest,
                                                                    UriComponentsBuilder componentsBuilder,
                                                                    BindingResult result) {

        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.createContract(contractDtoRequest);

        if (contractDtoResponse != null) {
            URI uri = componentsBuilder
                    .path(String.format("/contracts/%s", contractDtoResponse.getId()))
                    .buildAndExpand(contractDtoResponse)
                    .toUri();

            return ResponseEntity.created(uri).body(contractDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }


    @Override
    public ResponseEntity<? extends ContractBaseDto> updateContract(ContractDtoRequest contractDtoRequest, UUID id,
                                                                    BindingResult result) {

        if (result.hasErrors()) {
            contractDtoRequest.setErrors(result.getAllErrors());
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }

        ContractDtoResponse contractDtoResponse = contractService.updateContract(contractDtoRequest);

        if (contractDtoResponse != null) {
            return ResponseEntity.ok().body(contractDtoResponse);
        } else {
            return ResponseEntity.unprocessableEntity().body(contractDtoRequest);
        }
    }


    @Override
    public ResponseEntity<ContractDtoResponse> deleteContract(UUID id) {
        if (contractService.deleteContract(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @InitBinder(value = "contractDtoRequest")
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(contractDtoRequestValidator);
    }

}
