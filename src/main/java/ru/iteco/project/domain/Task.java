package ru.iteco.project.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель данных представляющая задание для исполнения
 */
@Entity
@Table(name = "task")
public class Task extends CreateAtIdentified implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id задания */
    @Id
    @Column
    private UUID id;

    /*** Уникальный id заказчика */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    /*** Уникальный id исполнителя */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "executor_id")
    private User executor;

    /*** Имя задания */
    @Column(name = "title", nullable = false)
    private String title;

    /*** Описание задания */
    @Column(name = "description", nullable = false)
    private String description;

    /*** Дата и время до наступления которой необходимо выполнить задание */
    @Column(name = "task_completion_date", nullable = false)
    private LocalDateTime taskCompletionDate;

    /*** Стоимость исполнения задания */
    @Column(name = "price", nullable = false, precision = 1000, scale = 2)
    private BigDecimal price;

    /*** Статус задания */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus taskStatus;

    /*** Решение задания */
    @Column(name = "task_decision")
    private String taskDecision;


    public Task() {
    }

    public Task(UUID id, User customer, User executor, String title, String description, LocalDateTime taskCompletionDate,
                BigDecimal price, TaskStatus taskStatus, String taskDecision) {
        this.id = id;
        this.customer = customer;
        this.executor = executor;
        this.title = title;
        this.description = description;
        this.taskCompletionDate = taskCompletionDate;
        this.price = price;
        this.taskStatus = taskStatus;
        this.taskDecision = taskDecision;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
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

    public LocalDateTime getTaskCompletionDate() {
        return taskCompletionDate;
    }

    public void setTaskCompletionDate(LocalDateTime taskCompletionDate) {
        this.taskCompletionDate = taskCompletionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(String taskDecision) {
        this.taskDecision = taskDecision;
    }

    @Override
    public UUID getId() {
        return id;
    }

}
