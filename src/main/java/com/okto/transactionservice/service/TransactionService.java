package com.okto.transactionservice.service;

import com.okto.transactionservice.dto.CountryTransactionSummaryDTO;
import com.okto.transactionservice.dto.DailyTransactionSummaryDTO;
import com.okto.transactionservice.dto.TransactionRequestDTO;
import com.okto.transactionservice.entity.Transaction;
import com.okto.transactionservice.exception.DuplicateTransactionException;
import com.okto.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CountryService countryService;
    private final CountryCurrencyService countryCurrencyService;

    @Transactional
    public void ingestTransaction(TransactionRequestDTO request) {
        log.debug("Ingesting transaction for userId={}, countryCode={}, timestamp={}",
                request.getUserId(), request.getCountryCode(), request.getTimestamp());

        var user = userService.getUserOrThrow(request.getUserId());
        var country = countryService.getCountryOrThrow(request.getCountryCode());
        countryCurrencyService.validateCurrencyAllowed(country.getCode(), request.getCurrency());

        boolean duplicate = transactionRepository
                .existsByUser_IdAndCountry_CodeAndAmountAndCurrencyAndTimestamp(
                        user.getId(),
                        country.getCode(),
                        request.getAmount(),
                        request.getCurrency(),
                        request.getTimestamp()
                );

        if (duplicate) {
            log.warn("Duplicate transaction detected for userId={}, countryCode={}, timestamp={}",
                    user.getId(), country.getCode(), request.getTimestamp());
            throw new DuplicateTransactionException(user.getId(), country.getCode(), request.getTimestamp());
        }

        Transaction transaction = Transaction.builder()
                .user(user)
                .country(country)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .timestamp(request.getTimestamp())
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction saved for userId={}, countryCode={}, amount={}",
                user.getId(), country.getCode(), request.getAmount());
    }

    @Transactional(readOnly = true)
    public List<CountryTransactionSummaryDTO> getUserCountryStats(Long userId) {
        log.debug("Fetching country stats for userId={}", userId);
        userService.getUserOrThrow(userId); // ensures user exists
        return transactionRepository.findUserTransactionSummariesGroupedByCountry(userId);
    }

    @Transactional(readOnly = true)
    public DailyTransactionSummaryDTO getDailySummary(LocalDate date) {
        log.debug("Fetching daily transaction summary for date={}", date);
        List<DailyTransactionSummaryDTO> dailyTransactionSummary = transactionRepository.findDailyTransactionSummary(date);

        if (dailyTransactionSummary.isEmpty()) {
            log.info("No transactions found for date={}", date);
            return new DailyTransactionSummaryDTO(date, 0L, BigDecimal.ZERO);
        }

        log.info("Daily summary for {}: count={}, totalAmount={}",
                date, dailyTransactionSummary.getFirst().getTransactionCount(),
                dailyTransactionSummary.getFirst().getTotalAmount());

        return dailyTransactionSummary.getFirst();
    }
}
