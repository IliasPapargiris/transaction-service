package com.okto.transactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okto.transactionservice.dto.TransactionRequestDTO;
import com.okto.transactionservice.entity.Country;
import com.okto.transactionservice.entity.CountrySupportedCurrency;
import com.okto.transactionservice.entity.Currency;
import com.okto.transactionservice.entity.User;
import com.okto.transactionservice.fixtures.CountryFixture;
import com.okto.transactionservice.fixtures.CountrySupportedCurrencyFixture;
import com.okto.transactionservice.fixtures.CurrencyFixture;
import com.okto.transactionservice.fixtures.TransactionRequestDTOFixture;
import com.okto.transactionservice.repository.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Rollback
class TransactionControllerTest {

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


    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CountryRepository countryRepository;
    @Autowired private CountrySupportedCurrencyRepository countrySupportedCurrencyRepository;
    @Autowired private CurrencyRepository currencyRepository;

    private Long testUserId;

    @BeforeEach
    void setup() {

            transactionRepository.deleteAll();
            userRepository.deleteAll();
        Country greece = countryRepository.save(CountryFixture.getInstance().getGreece());
        Country germany = countryRepository.save(CountryFixture.getInstance().getGermany());

        Currency eur = currencyRepository.save(CurrencyFixture.getInstance().getEuro());

        CountrySupportedCurrency grEur = CountrySupportedCurrencyFixture.getInstance().create(greece, eur);
        CountrySupportedCurrency deEur = CountrySupportedCurrencyFixture.getInstance().create(germany, eur);
        countrySupportedCurrencyRepository.save(grEur);
        countrySupportedCurrencyRepository.save(deEur);


        User user = User.builder()
                .name("Ilias")
                .email("admin@example.com")
                .password(passwordEncoder.encode("securePassword"))
                .role(com.okto.transactionservice.entity.Role.ADMIN)
                .build();

        testUserId = userRepository.save(user).getId();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIngestTransactionSuccessfully() throws Exception {
        TransactionRequestDTO request = TransactionRequestDTOFixture.getInstance().getDefault();
        request.setUserId(testUserId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testIngestTransaction_ValidationError() throws Exception {
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO(
                null, "", java.math.BigDecimal.ZERO, "EU", null
        );

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.userId[0]").value("User ID is required"))
                .andExpect(jsonPath("$.fieldErrors.countryCode").isArray())
                .andExpect(jsonPath("$.fieldErrors.countryCode", Matchers.hasItems(
                        "Country code is required",
                        "Country code must be exactly 2 characters",
                        "Country code must be uppercase ISO 3166-1 alpha-2"
                )))
                .andExpect(jsonPath("$.fieldErrors.amount[0]").value("Amount must be at least 0.01"))
                .andExpect(jsonPath("$.fieldErrors.currency", Matchers.hasItems(
                        "Currency code must be exactly 3 characters",
                        "Currency code must be uppercase ISO 4217"
                )))
                .andExpect(jsonPath("$.fieldErrors.timestamp[0]").value("Timestamp is required"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserAggregatesSuccessfully() throws Exception {
        TransactionRequestDTO request1 = TransactionRequestDTOFixture.getInstance().getDefault();
        request1.setUserId(testUserId);

        TransactionRequestDTO request2 = TransactionRequestDTOFixture.getInstance().getAnother();
        request2.setUserId(testUserId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/transactions/aggregates/users/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryCode").value("GR"))
                .andExpect(jsonPath("$[0].transactionCount").value(2))
                .andExpect(jsonPath("$[0].totalAmount").value(350))  // 200 + 150
                .andExpect(jsonPath("$[0].averageAmount").value(175));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserAggregates_UserNotFound() throws Exception {
        mockMvc.perform(get("/transactions/aggregates/users/{userId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User Not Found"))
                .andExpect(jsonPath("$.message").value("User not found with id: 9999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDailySummarySuccessfully() throws Exception {
        TransactionRequestDTO request = TransactionRequestDTOFixture.getInstance().getDefault();
        request.setUserId(testUserId);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/transactions/aggregates/daily")
                        .param("date", "2025-05-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(1))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDailySummary_InvalidDateFormat() throws Exception {
        mockMvc.perform(get("/transactions/aggregates/daily")
                        .param("date", "30-04-2025"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Type Mismatch"))
                .andExpect(jsonPath("$.message").value("Failed to convert value '30-04-2025' to type 'LocalDate'"));
    }
}
