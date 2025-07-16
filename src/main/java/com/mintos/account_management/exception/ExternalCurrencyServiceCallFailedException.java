package com.mintos.account_management.exception;

public class ExternalCurrencyServiceCallFailedException extends RuntimeException {
    public ExternalCurrencyServiceCallFailedException(String message) {
        super(message);
    }
}
