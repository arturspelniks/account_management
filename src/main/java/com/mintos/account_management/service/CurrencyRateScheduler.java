package com.mintos.account_management.service;

import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.CurrencyRate;
import com.mintos.account_management.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumSet;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class CurrencyRateScheduler {

    private final CurrencyService currencyService;
    private final CurrencyRateRepository currencyRateRepository;

    @Scheduled(fixedRateString = "${scheduler.currency-rate-update-interval}")
    public void updateRates() {
        EnumSet.allOf(Currency.class).forEach(from ->
                EnumSet.allOf(Currency.class).stream()
                        .filter(to -> from != to)
                        .forEach(to -> updateRate(from, to))
        );
    }

    private void updateRate(Currency from, Currency to) {
        try {
            BigDecimal rate = currencyService.getExchangeRate(from, to);
            CurrencyRate currencyRate = currencyRateRepository
                    .findByFromCurrencyAndToCurrency(from, to)
                    .map(existing -> {
                        existing.setRate(rate);
                        return existing;
                    })
                    .orElseGet(() -> new CurrencyRate(from, to, rate));
            currencyRateRepository.save(currencyRate);
        } catch (Exception exception) {
            log.error("Failed to update rate from {} to {}: {}", from, to, exception.getMessage());
        }
    }
}