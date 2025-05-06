package com.okto.transactionservice.exception;

/**
 * Thrown when a country code is not found in the database.
 */
public class CountryNotFoundException extends RuntimeException {
    public CountryNotFoundException(String code) {
        super(String.format("Country not found with code: %s", code));
    }
}
