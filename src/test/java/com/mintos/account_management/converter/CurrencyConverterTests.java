package com.mintos.account_management.converter;

import com.mintos.account_management.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyConverterTests {

    private CurrencyConverter converter = new CurrencyConverter();

    @ParameterizedTest
    @CsvSource({
            "USD, USD",
            "EUR, EUR",
            "JPY, JPY",
            "CAD, CAD"
    })
    void convertToDatabaseColumnReturnsCurrencyString(Currency currency, String expected) {
        // given - when
        var result = converter.convertToDatabaseColumn(currency);

        // then
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "USD, USD",
            "EUR, EUR",
            "JPY, JPY",
            "CAD, CAD"
    })
    void convertToEntityAttributeReturnsCurrencyEnum(String value, Currency expected) {
        // given - when
        var result = converter.convertToEntityAttribute(value);

        // then
        assertEquals(expected, result);
    }

    @Test
    void convertToDatabaseColumnReturnsNullForNullInput() {
        // given - when
        var result = converter.convertToDatabaseColumn(null);

        // then
        assertEquals(null, result);
    }

    @Test
    void convertToEntityAttributeWithInvalidValue() {
        // given - when
        var result = converter.convertToEntityAttribute("INVALID");

        // then
        assertEquals(null, result);
    }

}
