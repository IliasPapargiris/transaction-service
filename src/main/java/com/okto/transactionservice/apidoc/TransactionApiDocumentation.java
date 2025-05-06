package com.okto.transactionservice.apidoc;

import com.okto.transactionservice.dto.CountryTransactionSummaryDTO;
import com.okto.transactionservice.dto.DailyTransactionSummaryDTO;
import com.okto.transactionservice.dto.TransactionRequestDTO;
import com.okto.transactionservice.util.JsonExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public interface TransactionApiDocumentation {

    @Operation(
            summary = "Ingest transaction",
            description = "Accepts a new transaction and stores it in the system."
    )
    @ApiResponse(responseCode = "201", description = "Transaction successfully ingested")
    @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = JsonExamples.VALIDATION_ERROR_JSON)))
    ResponseEntity<Void> ingestTransaction(
            @RequestBody(
                    description = "Transaction request payload",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = JsonExamples.TRANSACTION_INGEST_REQUEST_JSON)))
            TransactionRequestDTO request
    );

    @Operation(
            summary = "Get user aggregates by country",
            description = "Returns total volume, average amount, and transaction count per country for the given user."
    )
    @ApiResponse(responseCode = "200", description = "Aggregates returned successfully",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = JsonExamples.USER_AGGREGATES_RESPONSE_JSON)))
    @ApiResponse(responseCode = "404", description = JsonExamples.USER_NOT_FOUND_JSON)
    ResponseEntity<List<CountryTransactionSummaryDTO>> getUserAggregates(
            @Parameter (description = "User Id", example = "1")
            @PathVariable Long userId);

    @Operation(
            summary = "Get system-wide daily summary",
            description = "Returns total volume and transaction count for the specified date."
    )
    @ApiResponse(responseCode = "200", description = "Daily summary returned",
            content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = JsonExamples.DAILY_SUMMARY_RESPONSE_JSON)))
    @ApiResponse(responseCode = "400", description = JsonExamples.MISSING_PARAMETER_ERROR_JSON)
    ResponseEntity<DailyTransactionSummaryDTO> getDailySummary(
            @Parameter(description = "Date to summarize" , example = "2025-05-01")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    );
}
