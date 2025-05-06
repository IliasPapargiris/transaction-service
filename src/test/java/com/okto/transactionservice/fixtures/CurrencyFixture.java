package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.entity.Currency;

public class CurrencyFixture {

    private static CurrencyFixture instance;

    private CurrencyFixture() {}

    public static CurrencyFixture getInstance() {
        if (instance == null) {
            instance = new CurrencyFixture();
        }
        return instance;
    }

    public Currency getEuro() {
        return Currency.builder()
                .code("EUR")
                .name("Euro")
                .build();
    }

}
