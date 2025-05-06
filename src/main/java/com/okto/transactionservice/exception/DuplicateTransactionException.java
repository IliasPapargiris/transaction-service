package com.okto.transactionservice.exception;

import java.time.LocalDateTime;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(Long userId, String countryCode, LocalDateTime timestamp) {
        super(String.format("Duplicate transaction detected for user %d in country %s at %s",
                userId, countryCode, timestamp));
    }
}
