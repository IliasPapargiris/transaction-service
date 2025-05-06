package com.okto.transactionservice.repository;

import com.okto.transactionservice.entity.CountrySupportedCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountrySupportedCurrencyRepository extends JpaRepository<CountrySupportedCurrency, Long> {

    boolean existsByCountry_CodeAndCurrency_Code(String countryCode, String currencyCode);
}
