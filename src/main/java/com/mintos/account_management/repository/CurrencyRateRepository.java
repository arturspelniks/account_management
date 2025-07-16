package com.mintos.account_management.repository;

import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {

    Optional<CurrencyRate> findByFromCurrencyAndToCurrency(Currency from, Currency to);

}
