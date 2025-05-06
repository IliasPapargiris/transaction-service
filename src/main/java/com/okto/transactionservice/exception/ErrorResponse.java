package com.okto.transactionservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standard structure for returning error information in API responses.
 * Can represent both generic errors and validation-related errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * A short, general description of the error type (e.g., "Validation Failed", "User Not Found").
     */
    private String error;

    /**
     * A more detailed message describing the specific error.
     */
    private String message;

    /**
     * The corresponding HTTP status code.
     */
    private int status;

    /**
     * The exact time the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * A map of field-specific validation errors (optional).
     * Only populated for validation-related failures.
     */
    private Map<String, List<String>> fieldErrors;

    /**
     * Constructor for generic (non-validation) errors.
     */
    public ErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
