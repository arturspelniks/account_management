package com.mintos.account_management.service;

import com.mintos.account_management.config.CurrencyApiProperties;
import com.mintos.account_management.dto.CurrencyApiResponse;
import com.mintos.account_management.dto.CurrencyRateDto;
import com.mintos.account_management.exception.ExternalCurrencyServiceCallFailedException;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.CurrencyRate;
import com.mintos.account_management.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyApiProperties currencyApiProperties;
    private final RestTemplate restTemplate;
    private final CurrencyRateRepository currencyRateRepository;

    public BigDecimal getExchangeRate(Currency fromCurrency, Currency toCurrency) {
        URI uri = buildUri(fromCurrency, toCurrency);

        try {
            CurrencyApiResponse response = fetchCurrencyApiResponse(uri);

            CurrencyRateDto toCurrencyRate = response.getData().get(toCurrency);
            if (toCurrencyRate == null) {
                throw new IllegalArgumentException("Currency not supported: " + toCurrency);
            }

            return toCurrencyRate.getValue();
        } catch (Exception ex) {
            return fetchFallbackRate(fromCurrency, toCurrency);
        }
    }

    private URI buildUri(Currency fromCurrency, Currency toCurrency) {
        return UriComponentsBuilder.fromUriString(currencyApiProperties.getUrl())
                .queryParam("apikey", currencyApiProperties.getApiKey())
                .queryParam("currencies", toCurrency.name())
                .queryParam("base_currency", fromCurrency.name())
                .build()
                .toUri();
    }

    private CurrencyApiResponse fetchCurrencyApiResponse(URI uri) {
        var responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, CurrencyApiResponse.class);

        if (!responseEntity.getStatusCode().is2xxSuccessful() ||
                responseEntity.getBody() == null ||
                responseEntity.getBody().getData() == null) {
            throw new ExternalCurrencyServiceCallFailedException("Currency not supported: " + uri);
        }

        return responseEntity.getBody();
    }

    private BigDecimal fetchFallbackRate(Currency fromCurrency, Currency toCurrency) {
        return currencyRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .map(CurrencyRate::getRate)
                .orElseThrow(() -> new IllegalArgumentException("Currency not supported: " + toCurrency));
    }

}