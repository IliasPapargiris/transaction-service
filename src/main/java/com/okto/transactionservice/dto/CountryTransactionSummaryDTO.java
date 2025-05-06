package com.okto.transactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CountryTransactionSummaryDTO {

    private final String countryCode;
    private final Long transactionCount;
    private final BigDecimal totalAmount;
    private final BigDecimal averageAmount;

    public CountryTransactionSummaryDTO(String countryCode, Long transactionCount, BigDecimal totalAmount, Double averageAmount) {
        this.countryCode = countryCode;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
        this.averageAmount = BigDecimal.valueOf(averageAmount);
    }
}
