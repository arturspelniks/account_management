package com.mintos.account_management.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "am_currency_rates")
public class CurrencyRate extends BaseEntity {

    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rate;

}
