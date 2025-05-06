package com.okto.transactionservice.exception;

/**
 * Thrown when the provided currency is not supported for the specified country.
 */
public class InvalidCurrencyForCountryException extends RuntimeException {
    public InvalidCurrencyForCountryException(String currency, String countryCode) {
        super(String.format("Currency %s is not supported for country %s", currency, countryCode));
    }
}
