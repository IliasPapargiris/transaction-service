package com.okto.transactionservice.repository;

import com.okto.transactionservice.dto.CountryTransactionSummaryDTO;
import com.okto.transactionservice.dto.DailyTransactionSummaryDTO;
import com.okto.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query("""
                SELECT new com.okto.transactionservice.dto.CountryTransactionSummaryDTO(
                    t.country.code,
                    COUNT(t),
                    SUM(t.amount),
                    AVG(t.amount)
                )
                FROM Transaction t
                WHERE t.user.id = :userId
                GROUP BY t.country.code
            """)
    List<CountryTransactionSummaryDTO> findUserTransactionSummariesGroupedByCountry(Long userId);



    @Query("""
                SELECT new com.okto.transactionservice.dto.DailyTransactionSummaryDTO(
                    CAST(DATE(t.timestamp) AS date), COUNT(t), COALESCE(SUM(t.amount), 0)
                )
                FROM Transaction t
                WHERE DATE(t.timestamp) = :date
                GROUP BY DATE(t.timestamp)
            """)
    List<DailyTransactionSummaryDTO> findDailyTransactionSummary(@Param("date") LocalDate date);


    boolean existsByUser_IdAndCountry_CodeAndAmountAndCurrencyAndTimestamp(
            Long userId,
            String countryCode,
            BigDecimal amount,
            String currency,
            LocalDateTime timestamp
    );


}
