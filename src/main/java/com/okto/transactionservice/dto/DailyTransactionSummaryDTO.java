package com.okto.transactionservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;


@Data
@AllArgsConstructor
public class DailyTransactionSummaryDTO {

    private LocalDate date;
    private Long transactionCount;
    private BigDecimal totalAmount;


    public DailyTransactionSummaryDTO(Date date, Long transactionCount, BigDecimal totalAmount) {
        this.date = date.toLocalDate(); // Convert to LocalDate
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
    }



}


