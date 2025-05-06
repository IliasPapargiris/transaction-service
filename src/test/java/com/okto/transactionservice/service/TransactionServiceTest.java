package com.okto.transactionservice.service;

import com.okto.transactionservice.dto.TransactionRequestDTO;
import com.okto.transactionservice.entity.Country;
import com.okto.transactionservice.entity.CountrySupportedCurrency;
import com.okto.transactionservice.entity.Currency;
import com.okto.transactionservice.entity.User;
import com.okto.transactionservice.exception.DuplicateTransactionException;
import com.okto.transactionservice.fixtures.CountryFixture;
import com.okto.transactionservice.fixtures.CountrySupportedCurrencyFixture;
import com.okto.transactionservice.fixtures.CurrencyFixture;
import com.okto.transactionservice.fixtures.TransactionRequestDTOFixture;
import com.okto.transactionservice.fixtures.UserFixture;
import com.okto.transactionservice.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Rollback
class TransactionServiceTest {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        System.out.println("🚀 Testcontainers PostgreSQL is running at: " + postgresContainer.getJdbcUrl());
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private TransactionService transactionService;
    @Autowired private UserRepository userRepository;
    @Autowired private CountryRepository countryRepository;
    @Autowired private CurrencyRepository currencyRepository;
    @Autowired private CountrySupportedCurrencyRepository countrySupportedCurrencyRepository;
    @Autowired private TransactionRepository transactionRepository;

    private User user;
    private Country country;
    private Currency currency;
    private TransactionRequestDTO request;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        countrySupportedCurrencyRepository.deleteAll();
        currencyRepository.deleteAll();
        countryRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(UserFixture.getInstance().getAdmin());
        country = countryRepository.save(CountryFixture.getInstance().getGreece());
        currency = currencyRepository.save(CurrencyFixture.getInstance().getEuro());

        countrySupportedCurrencyRepository.save(
                CountrySupportedCurrencyFixture.getInstance().create(country, currency)
        );

        request = TransactionRequestDTOFixture.getInstance().getDefault();
        request.setUserId(user.getId());
    }

    @Test
    void testIngestTransactionSuccessfully() {
        transactionService.ingestTransaction(request);
        assertThat(transactionRepository.count()).isEqualTo(1);
    }

    @Test
    void testDuplicateTransactionThrowsException() {
        transactionService.ingestTransaction(request);
        assertThrows(DuplicateTransactionException.class, () -> transactionService.ingestTransaction(request));
    }
}
