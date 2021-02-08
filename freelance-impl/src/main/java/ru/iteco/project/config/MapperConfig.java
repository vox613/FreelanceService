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
import ru.iteco.project.domain.*;
import ru.iteco.project.domain.audit.AuditEvent;
import ru.iteco.project.dto.AuditEventDto;
import ru.iteco.project.exception.EntityRecordNotFoundException;
import ru.iteco.project.exception.InvalidTaskStatusException;
import ru.iteco.project.exception.InvalidUserRoleException;
import ru.iteco.project.exception.InvalidUserStatusException;
import ru.iteco.project.repository.*;
import ru.iteco.project.resource.dto.*;
import ru.iteco.project.service.mappers.DateTimeMapper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDateTime;

import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.ON_CHECK;
import static ru.iteco.project.domain.TaskStatus.TaskStatusEnum.REGISTERED;
import static ru.iteco.project.domain.UserRole.UserRoleEnum.*;
import static ru.iteco.project.domain.UserStatus.UserStatusEnum.ACTIVE;

/**
 * Класс - конфигурация для Orika маппера
 */
@Configuration
public class MapperConfig implements OrikaMapperFactoryConfigurer {

    @Value("${errors.user.role.operation.unavailable}")
    private String unavailableOperationMessage;

    @Value("${errors.user.role.invalid}")
    private String userRoleIsInvalidMessage;

    @Value("${errors.task.status.invalid}")
    private String invalidTaskStatusMessage;

    @Value("${errors.contract.status.invalid}")
    private String invalidContractStatusMessage;


    /*** Объект доступа к репозиторию заданий */
    private final TaskRepository taskRepository;

    /*** Объект доступа к репозиторию пользователей */
    private final UserRepository userRepository;

    /*** Объект доступа к репозиторию ролей пользователей */
    private final UserRoleRepository userRoleRepository;

    /*** Объект доступа к репозиторию статусов пользователей */
    private final UserStatusRepository userStatusRepository;

    /*** Объект доступа к репозиторию статусов заданий */
    private final TaskStatusRepository taskStatusRepository;


    public MapperConfig(TaskRepository taskRepository, UserRepository userRepository,
                        UserRoleRepository userRoleRepository, UserStatusRepository userStatusRepository,
                        TaskStatusRepository taskStatusRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userStatusRepository = userStatusRepository;
        this.taskStatusRepository = taskStatusRepository;
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

        userMapperConfigure(mapperFactory);
        userStatusMapperConfigure(mapperFactory);
        userRoleMapperConfigure(mapperFactory);
        taskMapperConfigure(mapperFactory);
        taskStatusMapperConfigure(mapperFactory);
        contractMapperConfigure(mapperFactory);
        contractStatusMapperConfigure(mapperFactory);
        auditEventMapperConfigure(mapperFactory);
        bookKeepingReportDtoMapperConfigure(mapperFactory);
        bookKeepingClientReportDtoMapperConfigure(mapperFactory);
        bookKeepingTaskReportDtoMapperConfigure(mapperFactory);
    }


    /**
     * Метод конфигурирует маппер для преобразований: User --> UserDtoResponse,
     * UserDtoRequest --> User, User --> UserBaseDto
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void userMapperConfigure(MapperFactory mapperFactory) {
        // GET  User --> UserDtoResponse
        mapperFactory
                .classMap(User.class, UserDtoResponse.class)
                .byDefault()
                .customize(new CustomMapper<User, UserDtoResponse>() {
                    @Override
                    public void mapAtoB(User user, UserDtoResponse userDtoResponse, MappingContext context) {
                        taskRepository.findTasksByUser(user)
                                .forEach(task -> userDtoResponse.getTasksIdList().add(task.getId()));
                        userDtoResponse.setRole(user.getRole().getValue());
                        userDtoResponse.setUserStatus(user.getUserStatus().getValue());
                    }
                })
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  UserDtoRequest --> User
        mapperFactory
                .classMap(UserDtoRequest.class, User.class)
                .byDefault()
                .customize(new CustomMapper<UserDtoRequest, User>() {
                    @Override
                    public void mapAtoB(UserDtoRequest userDtoRequest, User user, MappingContext context) {
                        if (user.getRole().getId() == null) {
                            user.setRole(userRoleRepository.findUserRoleByValue(userDtoRequest.getRole())
                                    .orElseThrow(() -> new InvalidUserRoleException(userRoleIsInvalidMessage)));
                        }
                        user.setUserStatus(userStatusRepository.findUserStatusByValue(userDtoRequest.getUserStatus())
                                .orElseThrow(() -> new InvalidUserStatusException(unavailableOperationMessage)));
                    }
                })
                .register();

        // GET  User --> UserBaseDto
        mapperFactory
                .classMap(User.class, UserBaseDto.class)
                .byDefault()
                .customize(new CustomMapper<User, UserBaseDto>() {
                    @Override
                    public void mapAtoB(User user, UserBaseDto userBaseDto, MappingContext context) {
                        userBaseDto.setRole(user.getRole().getValue());
                        userBaseDto.setUserStatus(user.getUserStatus().getValue());
                    }
                })
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: UserStatus --> UserStatusDtoResponse,
     * UserStatusDtoRequest --> UserStatus
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void userStatusMapperConfigure(MapperFactory mapperFactory) {
        // GET  UserStatus --> UserStatusDtoResponse
        mapperFactory
                .classMap(UserStatus.class, UserStatusDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  UserStatusDtoRequest --> UserStatus
        mapperFactory
                .classMap(UserStatusDtoRequest.class, UserStatus.class)
                .byDefault()
                .register();
    }

    /**
     * Метод конфигурирует маппер для преобразований: UserRole --> UserRoleDtoResponse,
     * UserRoleDtoRequest --> UserRole
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void userRoleMapperConfigure(MapperFactory mapperFactory) {
        // GET  UserRole --> UserRoleDtoResponse
        mapperFactory
                .classMap(UserRole.class, UserRoleDtoResponse.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();

        // POST/PUT  UserRoleDtoRequest --> UserRole
        mapperFactory
                .classMap(UserStatusDtoRequest.class, UserStatus.class)
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
                        User user = userRepository.findById(taskDtoRequest.getUserId()).orElseThrow(
                                () -> new EntityRecordNotFoundException("errors.user.notfound")
                        );
                        if (isEqualsUserRole(CUSTOMER, user)) {
                            task.setTitle(taskDtoRequest.getTitle());
                            task.setDescription(taskDtoRequest.getDescription());
                            task.setTaskCompletionDate(DateTimeMapper.stringToObject(taskDtoRequest.getTaskCompletionDate()));
                            task.setPrice(taskDtoRequest.getPrice());

                            task.setTaskStatus(taskStatusRepository
                                    .findTaskStatusByValue((taskDtoRequest.getTaskStatus() != null) ?
                                            taskDtoRequest.getTaskStatus() : REGISTERED.name()
                                    ).orElseThrow(() -> new InvalidTaskStatusException(invalidTaskStatusMessage)));

                            task.setTaskDecision(taskDtoRequest.getTaskDecision());
                            user.setUserStatus(userStatusRepository.findUserStatusByValue(ACTIVE.name())
                                    .orElseThrow(InvalidUserRoleException::new));
                            task.setCustomer(user);

                        } else if (isEqualsUserRole(EXECUTOR, user)) {
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
     * Метод конфигурирует маппер для преобразований: AuditEvent <--> AuditEventDto
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void auditEventMapperConfigure(MapperFactory mapperFactory) {
        mapperFactory.classMap(AuditEvent.class, AuditEventDto.class)
                .customize(new CustomMapper<AuditEvent, AuditEventDto>() {
                    @Override
                    public void mapAtoB(AuditEvent auditEvent, AuditEventDto auditEventDto, MappingContext context) {
                        auditEventDto.setParams(auditEvent.getParams());
                        auditEventDto.setReturnValue(auditEvent.getReturnValue());
                    }

                    @Override
                    public void mapBtoA(AuditEventDto auditEventDto, AuditEvent auditEvent, MappingContext context) {
                        auditEvent.setParams(auditEventDto.getParams());
                        auditEvent.setReturnValue(auditEventDto.getReturnValue());
                    }
                })
                .byDefault()
                .register();
    }


    /**
     * Метод конфигурирует маппер для преобразований: Contract --> BookKeepingReportDto
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void bookKeepingReportDtoMapperConfigure(MapperFactory mapperFactory) {
        mapperFactory.classMap(Contract.class, BookKeepingReportDto.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .customize(new CustomMapper<Contract, BookKeepingReportDto>() {
                    @Override
                    public void mapAtoB(Contract contract, BookKeepingReportDto bookKeepingReportDto, MappingContext context) {
                        bookKeepingReportDto.setId(contract.getId());
                        bookKeepingReportDto.setContractStatus(contract.getContractStatus().getValue());
                    }
                })
                .register();
    }


    /**
     * Метод конфигурирует маппер для преобразований: User --> BookKeepingReportDto.ClientReportData
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void bookKeepingClientReportDtoMapperConfigure(MapperFactory mapperFactory) {
        mapperFactory.classMap(User.class, BookKeepingReportDto.ClientReportData.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .register();
    }


    /**
     * Метод конфигурирует маппер для преобразований: Task --> BookKeepingReportDto.TaskReportData
     *
     * @param mapperFactory - объект фабрики маппера, используется для настройки и регистрации моделей,
     *                      которые будут использоваться для выполнения функции отображения
     */
    private void bookKeepingTaskReportDtoMapperConfigure(MapperFactory mapperFactory) {
        mapperFactory.classMap(Task.class, BookKeepingReportDto.TaskReportData.class)
                .byDefault()
                .fieldMap("createdAt").converter("dateTimeFormatter").add()
                .fieldMap("updatedAt").converter("dateTimeFormatter").add()
                .customize(new CustomMapper<Task, BookKeepingReportDto.TaskReportData>() {
                    @Override
                    public void mapAtoB(Task task, BookKeepingReportDto.TaskReportData taskReportData, MappingContext context) {
                        taskReportData.setCustomerId(task.getCustomer().getId());
                        taskReportData.setExecutorId(task.getExecutor().getId());
                    }
                })
                .register();
    }


    /**
     * Класс-конвертер для преобразования типов и форматов дат
     */
    static class DateTimeFormatter extends BidirectionalConverter<LocalDateTime, String> {

        @Override
        public String convertTo(LocalDateTime source, Type<String> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.objectToString(source);
        }

        @Override
        public LocalDateTime convertFrom(String source, Type<LocalDateTime> destinationType, MappingContext mappingContext) {
            return DateTimeMapper.stringToObject(source);
        }
    }
}
