package ru.iteco.project.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Модель данных представляющая пользователей
 */
@Entity
@Table(schema = "freelance", name = "clients")
public class Client extends CreateAtIdentified implements Identified<UUID> {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id пользователя */
    @Id
    @Column
    private UUID id;

    /*** Имя пользователя */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /*** Фамилия пользователя */
    @Column(name = "second_name")
    private String secondName;

    /*** Отчество пользователя */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /*** Email пользователя */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /*** Номер телефона пользователя */
    @Column(name = "phone_number")
    private String phoneNumber;

    /*** Роль пользователя */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private ClientRole clientRole;

    /*** Статус пользователя */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id", nullable = false)
    private ClientStatus clientStatus;

    /*** Кошелек пользователя */
    @Column(name = "wallet", nullable = false, precision = 1000, scale = 2)
    private BigDecimal wallet = new BigDecimal(0);


    public Client() {
    }

    public Client(UUID id, String firstName, String secondName, String lastName,
                  String email, String phoneNumber, ClientRole clientRole, ClientStatus clientStatus, BigDecimal wallet) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.clientRole = clientRole;
        this.clientStatus = clientStatus;
        this.wallet = wallet;
    }

    @Override
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

    public ClientRole getClientRole() {
        return clientRole;
    }

    public void setClientRole(ClientRole role) {
        this.clientRole = role;
    }

    public ClientStatus getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public BigDecimal getWallet() {
        return wallet;
    }

    public void setWallet(BigDecimal wallet) {
        this.wallet = wallet;
    }

}
