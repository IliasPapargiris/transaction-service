package com.okto.transactionservice.service;

import com.okto.transactionservice.entity.Country;
import com.okto.transactionservice.exception.CountryNotFoundException;
import com.okto.transactionservice.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    /**
     * Retrieves a country by code or throws an exception if not found.
     *
     * @param code the country code
     * @return the found Country entity
     * @throws CountryNotFoundException if the country is not found
     */
    public Country getCountryOrThrow(String code) {
        String upperCode = code.toUpperCase();
        log.debug("Looking up country with code: {}", upperCode);

        return countryRepository.findById(upperCode)
                .orElseThrow(() -> {
                    log.warn("Country with code={} not found", upperCode);
                    return new CountryNotFoundException(code);
                });
    }
}
