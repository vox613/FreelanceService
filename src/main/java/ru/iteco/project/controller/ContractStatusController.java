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
import ru.iteco.project.controller.dto.ContractStatusBaseDto;
import ru.iteco.project.controller.dto.ContractStatusDtoRequest;
import ru.iteco.project.controller.dto.ContractStatusDtoResponse;
import ru.iteco.project.controller.searching.ContractStatusSearchDto;
import ru.iteco.project.controller.searching.PageDto;
import ru.iteco.project.service.ContractStatusService;
import ru.iteco.project.validator.ContractStatusDtoRequestValidator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует функционал слоя контроллеров для взаимодействия с ContractStatus
 */
@RestController
@RequestMapping(path = "/api/v1/statuses/contracts")
public class ContractStatusController {

    /*** Объект сервисного слоя для ContractStatus*/
    private final ContractStatusService contractStatusService;

    /*** Объект валидатора для ContractStatusDtoRequest*/
    private final ContractStatusDtoRequestValidator contractStatusDtoRequestValidator;


    public ContractStatusController(ContractStatusService contractStatusService, ContractStatusDtoRequestValidator contractStatusDtoRequestValidator) {
        this.contractStatusService = contractStatusService;
        this.contractStatusDtoRequestValidator = contractStatusDtoRequestValidator;
    }

    /**
     * Контроллер возвращает список всех статусов контрактов
     *
     * @return - список ContractStatusDtoResponse
     */
    @GetMapping
    ResponseEntity<List<ContractStatusDtoResponse>> getAllContractStatus() {
        ArrayList<ContractStatusDtoResponse> allContractStatuses = contractStatusService.getAllContractsStatuses();
        return ResponseEntity.ok().body(allContractStatuses);
    }


    /**
     * Контроллер возвращает ContractStatusDtoResponse статуса контракта с заданным id
     *
     * @param id - уникальный идентификатор статуса контракта
     * @return ContractStatusDtoResponse заданного статуса
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<ContractStatusDtoResponse> getContractStatus(@PathVariable UUID id) {
        ContractStatusDtoResponse contractStatusById = contractStatusService.getContractStatusById(id);
        if ((contractStatusById != null) && (contractStatusById.getId() != null)) {
            return ResponseEntity.ok().body(contractStatusById);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * Эндпоинт с реализацией пагинации и сортировки результатов поиска
     *
     * @param contractStatusSearchDto - dto объект который задает значения полей по которым будет осуществляться поиск данных
     * @param pageable                - объект пагинации с информацией о размере/наполнении/сортировке данных на странице
     * @return - объект PageDto с результатами соответствующими критериям запроса
     */
    @GetMapping(path = "/search")
    public PageDto<ContractStatusDtoResponse> getContracts(@RequestBody(required = false) ContractStatusSearchDto contractStatusSearchDto,
                                                           @PageableDefault(size = 5,
                                                                   page = 0,
                                                                   sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable) {

        return contractStatusService.getStatus(contractStatusSearchDto, pageable);
    }


    /**
     * Создает новый статус контракта
     *
     * @param contractStatusDtoRequest - тело запроса на создание статуса контракта
     * @return Тело запроса на создание статуса контракта с уникальным проставленным id,
     * * или тело запроса с id = null, если создать статус не удалось
     */
    @PostMapping
    public ResponseEntity<? extends ContractStatusBaseDto> createContractStatus(@Validated @RequestBody ContractStatusDtoRequest contractStatusDtoRequest,
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


    /**
     * Обновляет существующий статус контракта {id}
     *
     * @param id                       - уникальный идентификатор статуса контракта
     * @param contractStatusDtoRequest - тело запроса с данными для обновления
     */
    @PutMapping(value = "/{id}")
    public ResponseEntity<? extends ContractStatusBaseDto> updateTaskStatus(@PathVariable UUID id,
                                                                            @Validated @RequestBody ContractStatusDtoRequest contractStatusDtoRequest,
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


    /**
     * Удаляет статус контракта с заданным id
     *
     * @param id - уникальный идентификатор статуса контракта для удаления
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteTaskStatus(@PathVariable UUID id) {
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
