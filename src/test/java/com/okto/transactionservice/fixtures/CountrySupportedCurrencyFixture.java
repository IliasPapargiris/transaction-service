package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.entity.Country;
import com.okto.transactionservice.entity.CountrySupportedCurrency;
import com.okto.transactionservice.entity.Currency;

public class CountrySupportedCurrencyFixture {

    private static CountrySupportedCurrencyFixture instance;

    private CountrySupportedCurrencyFixture() {}

    public static CountrySupportedCurrencyFixture getInstance() {
        if (instance == null) {
            instance = new CountrySupportedCurrencyFixture();
        }
        return instance;
    }

    public CountrySupportedCurrency create(Country country, Currency currency) {
        return CountrySupportedCurrency.builder()
                .country(country)
                .currency(currency)
                .build();
    }
}
