package ru.iteco.project.config;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.iteco.project.domain.*;
import ru.iteco.project.exception.*;
import ru.iteco.project.repository.*;
import ru.iteco.project.resource.dto.*;
import ru.iteco.project.service.mappers.DateTimeMapper;
import ru.iteco.project.service.util.AuthenticationUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;

import static ru.iteco.project.domain.ClientRole.ClientRoleEnum.*;
import static ru.iteco.project.domain.ClientStatus.ClientStatusEnum.ACTIVE;
import static ru.iteco.project.domain.ContractStatus.ContractStatusEnum.PAID;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.IN_PROGRESS;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.ON_CHECK;

/**
 * Класс - конфигурация для Orika маппера
 */
@Configuration
@PropertySource(value = {"classpath:application.yml", "classpath:errors.properties"}, encoding = "UTF-8")
public class MapperConfig implements OrikaMapperFactoryConfigurer {

    @Value("${errors.client.role.operation.unavailable}")
    private String unavailableOperationMessage;

    @Value("${errors.client.role.invalid}")
    private String clientRoleIsInvalidMessage;

    @Value("${errors.task.status.invalid}")
    private String invalidTaskStatusMessage;

    @Value("${errors.contract.status.invalid}")
    private String invalidContractStatusMessage;

    /*** Установленный формат даты и времени*/
    @Value("${format.date.time}")
    private String formatDateTime;


    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final ClientRepository clientRepository;

    /*** Объект доступа к репозиторию ролей пользователей */
    private final ClientRoleRepository clientRoleRepository;

    /*** Объект доступа к репозиторию статусов пользователей */
    private final ClientStatusRepository clientStatusRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;

    /*** Объект доступа к репозиторию статусов контрактов */
    private final ContractStatusRepository contractStatusRepository;


    public MapperConfig(TaskRepository taskRepository, ClientRepository clientRepository,
                        ClientRoleRepository clientRoleRepository, ClientStatusRepository clientStatusRepository,
                        TaskStatusRepository taskStatusRepository, ContractStatusRepository contractStatusRepository) {
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
        this.clientRoleRepository = clientRoleRepository;
        this.clientStatusRepository = clientStatusRepository;
        this.taskStatusRepository = taskStatusRepository;
        this.contractStatusRepository = contractStatusRepository;
    }


    @Bean
    DatatypeFactory datatypeFactory() throws DatatypeConfigurationException {
        return DatatypeFactory.newInstance();
    }

    @Bean
    MappingContext.Factory mappingFactory() {
        MappingContext.Factory factory = new MappingContext.Factory();
        new DefaultMapperFactory.Builder().mappingContextFactory(factory).build();
        return factory;
    }


    @Override
    public void configure(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter("dateTimeFormatter", new DateTimeFormatter());

        clientMapperConfigure(mapperFactory);
        clientStatusMapperConfigure(mapperFactory);
        clientRoleMapperConfigure(mapperFactory);
        taskMapperConfigure(mapperFactory);
        taskStatusMapperConfigure(mapperFactory);
        contractMapperConfigure(mapperFactory);
        contractStatusMapperConfigure(mapperFactory);
    }


    /**
     * Метод конфигурирует маппер для преобразований: Client --> ClientDtoResponse,
     * ClientDtoRequest --> Client, Client --> ClientBaseDto
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void clientMapperConfigure(MapperFactory mapperFactory) {
        // GET  Client --> ClientDtoResponse
        mapperFactory
                .classMap(Client.class, ClientDtoResponse.class)
                .byDefault()
                .customize(new CustomMapper<Client, ClientDtoResponse>() {
                    @Override
                    public void mapAtoB(Client client, ClientDtoResponse clientDtoResponse, MappingContext context) {
                        taskRepository.findTasksByClient(client)
                                .forEach(task -> clientDtoResponse.getTasksIdList().add(task.getId()));
                        clientDtoResponse.setClientRole(client.getClientRole().getValue());
                        clientDtoResponse.setClientStatus(client.getClientStatus().getValue());
                    }
                })
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  ClientDtoRequest --> Client
        mapperFactory
                .classMap(ClientDtoRequest.class, Client.class)
                .byDefault()
                .customize(new CustomMapper<ClientDtoRequest, Client>() {
                    @Override
                    public void mapAtoB(ClientDtoRequest clientDtoRequest, Client client, MappingContext context) {
                        if (client.getClientRole().getId() == null) {
                            client.setClientRole(clientRoleRepository.findClientRoleByValue(clientDtoRequest.getClientRole())
                                    .orElseThrow(() -> new InvalidClientRoleException(clientRoleIsInvalidMessage)));
                        }
                        if (client.getClientStatus().getId() == null) {
                            client.setClientStatus(clientStatusRepository.findClientStatusByValue(clientDtoRequest.getClientStatus())
                                    .orElseThrow(() -> new InvalidClientStatusException(unavailableOperationMessage)));
                        }
                    }
                })
                .register();

        // GET  Client --> ClientBaseDto
        mapperFactory
                .classMap(Client.class, ClientBaseDto.class)
                .byDefault()
                .customize(new CustomMapper<Client, ClientBaseDto>() {
                    @Override
                    public void mapAtoB(Client client, ClientBaseDto clientBaseDto, MappingContext context) {
                        clientBaseDto.setClientRole(client.getClientRole().getValue());
                        clientBaseDto.setClientStatus(client.getClientStatus().getValue());
                    }
                })
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: ClientStatus --> ClientStatusDtoResponse,
     * ClientStatusDtoRequest --> ClientStatus
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void clientStatusMapperConfigure(MapperFactory mapperFactory) {
        // GET  ClientStatus --> ClientStatusDtoResponse
        mapperFactory
                .classMap(ClientStatus.class, ClientStatusDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  ClientStatusDtoRequest --> ClientStatus
        mapperFactory
                .classMap(ClientStatusDtoRequest.class, ClientStatus.class)
                .byDefault()
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: ClientRole --> ClientRoleDtoResponse,
     * ClientRoleDtoRequest --> ClientRole
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void clientRoleMapperConfigure(MapperFactory mapperFactory) {
        // GET  ClientRole --> ClientRoleDtoResponse
        mapperFactory
                .classMap(ClientRole.class, ClientRoleDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  ClientRoleDtoRequest --> ClientRole
        mapperFactory
                .classMap(ClientStatusDtoRequest.class, ClientStatus.class)
                .byDefault()
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: Task --> TaskDtoResponse,
     * TaskDtoRequest --> Task
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void taskMapperConfigure(MapperFactory mapperFactory) {
        // GET  Task --> TaskDtoResponse
        mapperFactory
                .classMap(Task.class, TaskDtoResponse.class)
                .byDefault()
                .customize(new CustomMapper<Task, TaskDtoResponse>() {
                    @Override
                    public void mapAtoB(Task task, TaskDtoResponse taskDtoResponse, MappingContext context) {
                        taskDtoResponse.setCustomerId(task.getCustomer().getId());
                        taskDtoResponse.setTaskStatus(task.getTaskStatus().getValue());
                        if (task.getExecutor() != null) {
                            taskDtoResponse.setExecutorId(task.getExecutor().getId());
                        }
                    }
                })
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .fieldMap("taskCompletionDate").converter("dateTimeFormatter").add()
                .register();


        // POST/PUT  TaskDtoRequest --> Task
        mapperFactory
                .classMap(TaskDtoRequest.class, Task.class)
                .customize(new CustomMapper<TaskDtoRequest, Task>() {
                    @Override
                    public void mapAtoB(TaskDtoRequest taskDtoRequest, Task task, MappingContext context) {
                        Client client = clientRepository.findById(AuthenticationUtil.getUserPrincipalId()).orElseThrow(
                                () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
                        );
                        if (isEqualsClientRole(CUSTOMER, client)) {
                            task.setTitle(taskDtoRequest.getTitle());
                            task.setDescription(taskDtoRequest.getDescription());
                            task.setTaskCompletionDate(DateTimeMapper.stringToObject(taskDtoRequest.getTaskCompletionDate(), formatDateTime));
                            task.setTaskStatus(taskStatusRepository
                                    .findTaskStatusByValue(taskDtoRequest.getTaskStatus())
                                    .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage))
                            );
                            if (task.getExecutor() == null) {
                                task.setPrice(taskDtoRequest.getPrice());
                            }
                            task.setTaskDecision(taskDtoRequest.getTaskDecision());
                            client.setClientStatus(clientStatusRepository.findClientStatusByValue(ACTIVE.name())
                                    .orElseThrow(InvalidClientRoleException::new));
                            task.setCustomer(client);

                        } else if (isEqualsClientRole(EXECUTOR, client)) {
                            task.setTaskDecision(taskDtoRequest.getTaskDecision());
                            task.setTaskStatus(taskStatusRepository.findTaskStatusByValue(ON_CHECK.name())
                                    .orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage)));
                        }
                    }
                })
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: TaskStatus --> TaskStatusDtoResponse,
     * TaskStatusDtoRequest --> TaskStatus
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void taskStatusMapperConfigure(MapperFactory mapperFactory) {
        // GET  TaskStatus --> TaskStatusDtoResponse
        mapperFactory
                .classMap(TaskStatus.class, TaskStatusDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  TaskStatusDtoRequest --> TaskStatus
        mapperFactory
                .classMap(TaskStatusDtoRequest.class, TaskStatus.class)
                .byDefault()
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: Contract --> ContractDtoResponse,
     * ContractDtoRequest --> Contract
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void contractMapperConfigure(MapperFactory mapperFactory) {
        // GET  Contract --> ContractDtoResponse
        mapperFactory
                .classMap(Contract.class, ContractDtoResponse.class)
                .byDefault()
                .customize(new CustomMapper<Contract, ContractDtoResponse>() {
                    @Override
                    public void mapAtoB(Contract contract, ContractDtoResponse contractDtoResponse, MappingContext context) {
                        contractDtoResponse.setExecutorId(contract.getExecutor().getId());
                        contractDtoResponse.setTaskId(contract.getTask().getId());
                        contractDtoResponse.setContractStatus(contract.getContractStatus().getValue());
                    }
                })
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();


        // POST/PUT  ContractDtoRequest --> Contract
        mapperFactory
                .classMap(ContractDtoRequest.class, Contract.class)
                .customize(new CustomMapper<ContractDtoRequest, Contract>() {
                    @Override
                    public void mapAtoB(ContractDtoRequest contractDtoRequest, Contract contract, MappingContext context) {

                        if (contract.getId() == null) {

                            Task task = taskRepository.findById(contractDtoRequest.getTaskId()).orElseThrow(
                                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
                            );
                            task.setTaskStatus(taskStatusRepository.findTaskStatusByValue(IN_PROGRESS.name())
                                    .orElseThrow(InvalidClientRoleException::new));

                            Client executor = clientRepository.findById(contractDtoRequest.getExecutorId()).orElseThrow(
                                    () -> new EntityRecordNotFoundException("errors.persistence.entity.notfound")
                            );
                            executor.setClientStatus(clientStatusRepository.findClientStatusByValue(ACTIVE.name())
                                    .orElseThrow(InvalidClientStatusException::new));

                            contract.setCustomer(task.getCustomer());
                            contract.setExecutor(executor);
                            task.setExecutor(executor);
                            contract.setTask(task);

                            contract.setContractStatus(contractStatusRepository.findContractStatusByValue(PAID.name())
                                    .orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
                        } else {
                            contract.setContractStatus(contractStatusRepository
                                    .findContractStatusByValue((contractDtoRequest.getContractStatus() != null) ?
                                            contractDtoRequest.getContractStatus() : PAID.name()
                                    ).orElseThrow(() -> new InvalidContractStatusException(invalidContractStatusMessage)));
                        }
                    }
                })
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: ContractStatus --> ContractStatusDtoResponse,
     * ContractStatusDtoRequest --> ContractStatus
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void contractStatusMapperConfigure(MapperFactory mapperFactory) {
        // GET  ContractStatus --> ContractStatusDtoResponse
        mapperFactory
                .classMap(ContractStatus.class, ContractStatusDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  ContractStatusDtoRequest --> ContractStatus
        mapperFactory
                .classMap(ContractStatusDtoRequest.class, ContractStatus.class)
                .byDefault()
                .register();
    }


    /**
     * Класс-конвертер для преобразования типов и форматов дат
     */
    class DateTimeFormatter extends BidirectionalConverter<LocalDateTime, String> {

        @Override
        public String convertTo(LocalDateTime source, Type<String> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.objectToString(source, formatDateTime);
        }

        @Override
        public LocalDateTime convertFrom(String source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.stringToObject(source, formatDateTime);
        }
    }
}
