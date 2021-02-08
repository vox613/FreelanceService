package ru.iteco.project.resource.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@ApiModel(description = "Данные модели бухгалтерского отчета")
public class BookKeepingReportDto implements Serializable {

    @ApiModelProperty(value = "Идентификатор контракта", example = "748b310e-486d-11eb-94e0-0242ac130002", allowEmptyValue = true)
    private UUID id;

    @ApiModelProperty(value = "Статус контракта", example = "DONE", required = true)
    private String contractStatus;

    @ApiModelProperty(value = "Дата и время создания контракта", example = "2020-12-28 03:47:32", required = true)
    private String createdAt;

    @ApiModelProperty(value = "Дата и время последнего обновления контракта", example = "2020-12-28 03:47:32", required = true)
    private String updatedAt;

    @ApiModelProperty(value = "Данные заказчика", example = "", required = true)
    private ClientReportData customer = new ClientReportData();

    @ApiModelProperty(value = "Данные исполнителя", example = "", required = true)
    private ClientReportData executor = new ClientReportData();

    @ApiModelProperty(value = "Информация о задании", example = "", required = true)
    private TaskReportData task = new TaskReportData();


    public BookKeepingReportDto(UUID id, String contractStatus, String createdAt, String updatedAt, ClientReportData customer,
                                ClientReportData executor, TaskReportData task) {
        this.id = id;
        this.contractStatus = contractStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.customer = customer;
        this.executor = executor;
        this.task = task;
    }

    public BookKeepingReportDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ClientReportData getCustomer() {
        return customer;
    }

    public void setCustomer(ClientReportData customer) {
        this.customer = customer;
    }

    public ClientReportData getExecutor() {
        return executor;
    }

    public void setExecutor(ClientReportData executor) {
        this.executor = executor;
    }

    public TaskReportData getTask() {
        return task;
    }

    public void setTask(TaskReportData task) {
        this.task = task;
    }

    @ApiModel(description = "Модель данных о клиенте для отчета")
    public static class ClientReportData implements Serializable {

        @ApiModelProperty(value = "Идентификатор пользователя", example = "748b310e-486d-11eb-94e0-0242ac130002",
                allowEmptyValue = true)
        private UUID id;

        @ApiModelProperty(value = "Имя пользователя", example = "firstName", required = true)
        private String firstName;

        @ApiModelProperty(value = "Отчество пользователя", example = "secondName", allowEmptyValue = true)
        private String secondName;

        @ApiModelProperty(value = "Фамилия пользователя", example = "lastName", required = true)
        private String lastName;

        @ApiModelProperty(value = "Логин пользователя", example = "login", required = true)
        private String login;

        @ApiModelProperty(value = "Email пользователя", example = "email@mail.com", required = true)
        private String email;

        @ApiModelProperty(value = "Номер телефона пользователя", example = "81234567898", required = true)
        private String phoneNumber;

        @ApiModelProperty(value = "Роль пользователя", example = "CUSTOMER", required = true,
                allowableValues = "ADMIN, CUSTOMER, EXECUTOR")
        private String role;

        @ApiModelProperty(value = "Статус пользователя", example = "ACTIVE", required = true,
                allowableValues = "NOT_EXIST, CREATED, BLOCKED, ACTIVE")
        private String userStatus;

        @ApiModelProperty(value = "Кошелек пользователя", example = "1500", required = true)
        private BigDecimal wallet = new BigDecimal(0);

        @ApiModelProperty(value = "Дата и время создания пользователя", example = "2020-12-28 03:47:32", required = true)
        private String createdAt;

        @ApiModelProperty(value = "Дата и время последнего обновления пользователя", example = "2020-12-28 03:47:32",
                required = true)
        private String updatedAt;

        public ClientReportData(UUID id, String firstName, String secondName, String lastName, String login, String email,
                                String phoneNumber, String role, String userStatus, BigDecimal wallet, String createdAt, String updatedAt) {
            this.id = id;
            this.firstName = firstName;
            this.secondName = secondName;
            this.lastName = lastName;
            this.login = login;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.role = role;
            this.userStatus = userStatus;
            this.wallet = wallet;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public ClientReportData() {
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
        }

        public BigDecimal getWallet() {
            return wallet;
        }

        public void setWallet(BigDecimal wallet) {
            this.wallet = wallet;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }


    @ApiModel(description = "Модель данных о задании для отчета")
    public static class TaskReportData implements Serializable {

        @ApiModelProperty(value = "Идентификатор задания", example = "748b310e-486d-11eb-94e0-0242ac130002",
                allowEmptyValue = true)
        private UUID id;

        @ApiModelProperty(value = "Идентификатор заказчика", example = "748b310e-486d-11eb-94e0-0242ac130002",
                required = true)
        private UUID customerId;

        @ApiModelProperty(value = "Идентификатор исполнителя", example = "bf51c162-95f3-4e69-ab6d-7ff214430ba6")
        private UUID executorId;

        @ApiModelProperty(value = "Название задания", example = "Задание...", required = true)
        private String title;

        @ApiModelProperty(value = "Описание задания", example = "Описание задания", required = true)
        private String description;

        @ApiModelProperty(value = "Статус задания", example = "REGISTERED", required = true,
                allowableValues = "REGISTERED, IN_PROGRESS, ON_CHECK, ON_FIX, DONE, CANCELED")
        private String taskStatus;

        @ApiModelProperty(value = "Крайние дата и время выполнения задания", example = "2020-12-28 03:47:32", required = true)
        private String taskCompletionDate;

        @ApiModelProperty(value = "Стоимость исполнения задания", example = "1000", required = true)
        private BigDecimal price;

        @ApiModelProperty(value = "Решение задания", example = "Решение задания", required = true, allowEmptyValue = true)
        private String taskDecision;

        @ApiModelProperty(value = "Дата и время создания задания", example = "2020-12-28 03:47:32", required = true)
        private String createdAt;

        @ApiModelProperty(value = "Дата и время последнего обновления задания", example = "2020-12-28 03:47:32", required = true)
        private String updatedAt;

        public TaskReportData(UUID id, UUID customerId, UUID executorId, String title, String description,
                              String taskStatus, String taskCompletionDate, BigDecimal price, String taskDecision,
                              String createdAt, String updatedAt) {
            this.id = id;
            this.customerId = customerId;
            this.executorId = executorId;
            this.title = title;
            this.description = description;
            this.taskStatus = taskStatus;
            this.taskCompletionDate = taskCompletionDate;
            this.price = price;
            this.taskDecision = taskDecision;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public TaskReportData() {
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public UUID getCustomerId() {
            return customerId;
        }

        public void setCustomerId(UUID customerId) {
            this.customerId = customerId;
        }

        public UUID getExecutorId() {
            return executorId;
        }

        public void setExecutorId(UUID executorId) {
            this.executorId = executorId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(String taskStatus) {
            this.taskStatus = taskStatus;
        }

        public String getTaskCompletionDate() {
            return taskCompletionDate;
        }

        public void setTaskCompletionDate(String taskCompletionDate) {
            this.taskCompletionDate = taskCompletionDate;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getTaskDecision() {
            return taskDecision;
        }

        public void setTaskDecision(String taskDecision) {
            this.taskDecision = taskDecision;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
