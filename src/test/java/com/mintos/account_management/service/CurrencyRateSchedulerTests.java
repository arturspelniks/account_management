package com.mintos.account_management.service;

import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.CurrencyRate;
import com.mintos.account_management.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CurrencyRateSchedulerTests {

    @MockitoBean
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    private CurrencyRateScheduler currencyRateScheduler;

    @BeforeEach
    void setUp() {
        currencyRateRepository.deleteAll();;
        currencyRateScheduler = new CurrencyRateScheduler(currencyService, currencyRateRepository);
    }

    @Test
    void schedulerExecutesUpdateRatesAutomatically() {
        // given
        when(currencyService.getExchangeRate(any(), any()))
                .thenReturn(new BigDecimal("1.25"));

        // when
        currencyRateScheduler.updateRates();

        // then
        List<CurrencyRate> savedRates = currencyRateRepository.findAll();
        assertFalse(savedRates.isEmpty());

        for (Currency from : Currency.values()) {
            for (Currency to : Currency.values()) {
                if (from != to) {
                    assertTrue(savedRates.stream().anyMatch(rate ->
                            rate.getFromCurrency() == from &&
                                    rate.getToCurrency() == to &&
                                    rate.getRate().compareTo(new BigDecimal("1.25")) == 0
                    ));
                }
            }
        }
    }

}
