package com.okto.transactionservice.fixtures;

import com.okto.transactionservice.entity.Country;

public class CountryFixture {

    private static CountryFixture instance;

    private CountryFixture() {}

    public static CountryFixture getInstance() {
        if (instance == null) {
            instance = new CountryFixture();
        }
        return instance;
    }

    private static class CountryBuilder {
        private String code;
        private String name;

        public CountryBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public CountryBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public Country build() {
            return Country.builder()
                    .code(this.code)
                    .name(this.name)
                    .build();
        }
    }

    public Country getGreece() {
        return new CountryBuilder()
                .withCode("GR")
                .withName("Greece")
                .build();
    }

    public Country getGermany() {
        return new CountryBuilder()
                .withCode("DE")
                .withName("Germany")
                .build();
    }
}
