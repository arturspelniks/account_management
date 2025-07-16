package com.mintos.account_management.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Currency {

    EUR("EUR"),
    USD("USD"),
    CAD("CAD"),
    JPY("JPY");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static Currency fromString(String text) {
        for (Currency e : Currency.values()) {
            if (e.value.equals(text)) {
                return e;
            }
        }

        return null;
    }

}
