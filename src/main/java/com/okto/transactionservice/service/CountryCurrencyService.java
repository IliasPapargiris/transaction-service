package com.okto.transactionservice.service;

import com.okto.transactionservice.exception.InvalidCurrencyForCountryException;
import com.okto.transactionservice.repository.CountrySupportedCurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryCurrencyService {

    private final CountrySupportedCurrencyRepository supportedCurrencyRepository;

    /**
     * Validates if a currency is allowed for a given country.
     *
     * @param countryCode  the ISO code of the country
     * @param currencyCode the ISO code of the currency
     * @throws InvalidCurrencyForCountryException if the currency is not supported in the country
     */
    public void validateCurrencyAllowed(String countryCode, String currencyCode) {
        log.debug("Validating currency={} for country={}", currencyCode, countryCode);

        boolean allowed = supportedCurrencyRepository.existsByCountry_CodeAndCurrency_Code(countryCode, currencyCode);
        if (!allowed) {
            log.warn("Invalid currency={} for country={}", currencyCode, countryCode);
            throw new InvalidCurrencyForCountryException(currencyCode, countryCode);
        }

        log.debug("Currency {} is valid for country {}", currencyCode, countryCode);
    }
}
