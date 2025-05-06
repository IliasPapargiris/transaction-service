package com.okto.transactionservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Centralized exception handling for the transaction service.
 * Formats all expected exceptions into {@link ErrorResponse}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Utility method to standardize error response creation
    private ErrorResponse createErrorResponse(String error, String message, HttpStatus status) {
        return new ErrorResponse(error, message, status.value(), LocalDateTime.now());
    }


    // DOMAIN-SPECIFIC EXCEPTIONS

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("User Not Found", ex.getMessage(), HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CountryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCountryNotFound(CountryNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Country Not Found", ex.getMessage(), HttpStatus.NOT_FOUND),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidCurrencyForCountryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrency(InvalidCurrencyForCountryException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Invalid Currency", ex.getMessage(), HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateTransaction(DuplicateTransactionException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Duplicate Transaction", ex.getMessage(), HttpStatus.CONFLICT),
                HttpStatus.CONFLICT
        );
    }



    // SECURITY-RELATED EXCEPTIONS

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Authentication Failed", "Invalid username or password", HttpStatus.UNAUTHORIZED),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Access Denied", "You do not have permission to access this resource.", HttpStatus.FORBIDDEN),
                HttpStatus.FORBIDDEN);
    }

    //  VALIDATION EXCEPTIONS

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        ErrorResponse response = new ErrorResponse();
        response.setError("Validation Failed");
        response.setMessage("Validation constraints failed for one or more fields.");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());
        response.setFieldErrors(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
        });

        ErrorResponse response = new ErrorResponse();
        response.setError("Constraint Violation");
        response.setMessage("Validation constraints failed for one or more parameters.");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTimestamp(LocalDateTime.now());
        response.setFieldErrors(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    //  GENERIC TECHNICAL EXCEPTIONS

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableJson(HttpMessageNotReadableException ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Malformed Request", "Request body is missing or malformed.", HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String msg = String.format("Failed to convert value '%s' to type '%s'",
                ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        return new ResponseEntity<>(
                createErrorResponse("Type Mismatch", msg, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST);
    }


    // UNHANDLED / FALLBACK

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, WebRequest request) {
        return new ResponseEntity<>(
                createErrorResponse("Internal Server Error", "An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
