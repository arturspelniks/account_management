package com.mintos.account_management.converter;

import com.mintos.account_management.model.Currency;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency value) {
        return value != null ? value.getValue() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String valueString) {
        return Currency.fromString(valueString);
    }

}
