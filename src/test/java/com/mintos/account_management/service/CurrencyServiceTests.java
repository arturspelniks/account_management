package com.mintos.account_management.service;

import com.mintos.account_management.config.CurrencyApiProperties;
import com.mintos.account_management.dto.CurrencyApiResponse;
import com.mintos.account_management.dto.CurrencyRateDto;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.repository.CurrencyRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CurrencyApiProperties currencyApiProperties;

    @Mock
    private CurrencyRateRepository currencyRateRepository;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        when(currencyApiProperties.getUrl()).thenReturn("http://api.example.com");
        when(currencyApiProperties.getApiKey()).thenReturn("testKey");
    }

    @Test
    void returnsExchangeRateForValidCurrencies() {
        // given
        Currency from = Currency.USD;
        Currency to = Currency.EUR;

        CurrencyApiResponse mockResponse = new CurrencyApiResponse();
        CurrencyRateDto rateDto = new CurrencyRateDto();
        rateDto.setValue(new BigDecimal("0.85"));
        mockResponse.setData(Map.of(to, rateDto));

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                eq(null),
                eq(CurrencyApiResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        // when
        BigDecimal exchangeRate = currencyService.getExchangeRate(from, to);

        // then
        assertEquals(new BigDecimal("0.85"), exchangeRate);
    }

    @Test
    void throwsExceptionForUnsupportedCurrency() {
        // given
        Currency from = Currency.USD;
        Currency to = Currency.JPY;

        CurrencyApiResponse mockResponse = new CurrencyApiResponse();
        mockResponse.setData(Map.of());

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                eq(null),
                eq(CurrencyApiResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));

        // when - then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            currencyService.getExchangeRate(from, to);
        });

        assertEquals("Currency not supported: JPY", exception.getMessage());
    }

    @Test
    void throwsExceptionForNullResponse() {
        // given
        Currency from = Currency.USD;
        Currency to = Currency.EUR;
        URI uri = URI.create("http://api.example.com?apikey=testKey&currencies=EUR&base_currency=USD");

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                eq(null),
                eq(CurrencyApiResponse.class)
        )).thenReturn(null);

        // when - then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            currencyService.getExchangeRate(from, to);
        });

        assertEquals("Currency not supported: EUR", exception.getMessage());
    }

    @Test
    void throwsExceptionForUnsuccessfulResponse() {
        // given
        Currency from = Currency.USD;
        Currency to = Currency.EUR;
        URI uri = URI.create("http://api.example.com?apikey=testKey&currencies=EUR&base_currency=USD");

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                eq(null),
                eq(CurrencyApiResponse.class)
        )).thenReturn(ResponseEntity.internalServerError().build());

        // when - then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            currencyService.getExchangeRate(from, to);
        });

        assertEquals("Currency not supported: EUR", exception.getMessage());
    }

}