package com.okto.transactionservice.exception;

/**
 * Thrown when a user is not found in the database.
 */

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super(String.format("User not found with id: %d", userId));
    }

    public UserNotFoundException(String emailOrUsername) {
        super(String.format("User not found with email or username: %s", emailOrUsername));
    }
}
