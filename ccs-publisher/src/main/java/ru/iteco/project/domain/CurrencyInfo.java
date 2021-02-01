package ru.iteco.project.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Модель данных представляющая данные о валюте
 */
@Entity
@Table(name = "exchange_rates")
public class CurrencyInfo extends CreateAtIdentified implements Serializable {

    private static final long serialVersionUID = -7931737332645464539L;

    /*** Уникальный id валюты */
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    /*** Цифровой идентификатор валюты */
    @Column(name = "num_code", nullable = false, unique = true)
    private String numCode;

    /*** Буквенный идентификатор валюты */
    @Column(name = "char_code", nullable = false)
    private String charCode;

    /*** Номинал валюты */
    @Column(name = "nominal", nullable = false)
    private BigDecimal nominal;

    /*** Название валюты */
    @Column(name = "name", nullable = false)
    private String name;

    /*** Текущий курс валюты относительно Рубля */
    @Column(name = "current_value", nullable = false)
    private BigDecimal currentValue;

    /*** Предыдущий курс валюты относительно Рубля */
    @Column(name = "previous_value", nullable = false)
    private BigDecimal previousValue;


    public CurrencyInfo() {
    }

    public CurrencyInfo(String id, String numCode, String charCode, BigDecimal nominal, String name, BigDecimal currentValue,
                        BigDecimal previousValue) {
        this.id = id;
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.name = name;
        this.currentValue = currentValue;
        this.previousValue = previousValue;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumCode() {
        return numCode;
    }

    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(BigDecimal previousValue) {
        this.previousValue = previousValue;
    }
}
