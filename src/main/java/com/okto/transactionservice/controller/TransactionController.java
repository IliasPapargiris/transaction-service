package com.okto.transactionservice.controller;

import com.okto.transactionservice.apidoc.TransactionApiDocumentation;
import com.okto.transactionservice.dto.CountryTransactionSummaryDTO;
import com.okto.transactionservice.dto.DailyTransactionSummaryDTO;
import com.okto.transactionservice.dto.TransactionRequestDTO;
import com.okto.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController implements TransactionApiDocumentation {

    private final TransactionService transactionService;


    @PostMapping
    @Override
    public ResponseEntity<Void> ingestTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        transactionService.ingestTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/aggregates/users/{userId}")
    @Override
    public ResponseEntity<List<CountryTransactionSummaryDTO>> getUserAggregates(@PathVariable Long userId) {
        List<CountryTransactionSummaryDTO> stats = transactionService.getUserCountryStats(userId);
        return ResponseEntity.ok(stats);
    }
    @GetMapping("/aggregates/daily")
    @Override
    public ResponseEntity<DailyTransactionSummaryDTO> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyTransactionSummaryDTO summary = transactionService.getDailySummary(date);
        return ResponseEntity.ok(summary);
    }
}
