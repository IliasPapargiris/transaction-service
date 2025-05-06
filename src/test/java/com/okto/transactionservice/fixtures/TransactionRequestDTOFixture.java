package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.dto.TransactionRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequestDTOFixture {

    private static TransactionRequestDTOFixture instance;

    private TransactionRequestDTOFixture() {}

    public static TransactionRequestDTOFixture getInstance() {
        if (instance == null) {
            instance = new TransactionRequestDTOFixture();
        }
        return instance;
    }

    public TransactionRequestDTO getDefault() {
        return new TransactionRequestDTO(
                1L,
                "GR",
                BigDecimal.valueOf(200.00),
                "EUR",
                LocalDateTime.of(2025, 5, 1, 12, 0)
        );
    }

    public TransactionRequestDTO getAnother() {
        return new TransactionRequestDTO(
                1L,
                "GR",
                BigDecimal.valueOf(150.00),
                "EUR",
                LocalDateTime.of(2025, 5, 2, 14, 30)
        );
    }

}
