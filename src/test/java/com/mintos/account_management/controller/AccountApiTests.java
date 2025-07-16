package com.mintos.account_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mintos.account_management.dto.CurrencyApiResponse;
import com.mintos.account_management.dto.CurrencyRateDto;
import com.mintos.account_management.dto.FundTransferDto;
import com.mintos.account_management.model.Account;
import com.mintos.account_management.model.Client;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.Transaction;
import com.mintos.account_management.repository.AccountRepository;
import com.mintos.account_management.repository.ClientRepository;
import com.mintos.account_management.repository.TransactionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ClientRepository clientRepository;

    @MockitoBean
    private RestTemplate restTemplate;

    private Client client;
    private Account account1;
    private Account account2;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        client = Client.builder().name("Test Client").build();
        clientRepository.save(client);

        account1 = Account.builder()
                .client(client)
                .balance(new BigDecimal("1000.00"))
                .currency(Currency.USD)
                .build();
        account1 = accountRepository.save(account1);

        account2 = Account.builder()
                .client(client)
                .balance(new BigDecimal("50.00"))
                .currency(Currency.EUR)
                .build();
        account2 = accountRepository.save(account2);

        CurrencyApiResponse mockResponse = new CurrencyApiResponse();
        Map<Currency, CurrencyRateDto> data = new EnumMap<>(Currency.class);
        data.put(Currency.CAD, new CurrencyRateDto(Currency.CAD, new BigDecimal("1.5986262348")));
        data.put(Currency.EUR, new CurrencyRateDto(Currency.EUR, new BigDecimal("1")));
        data.put(Currency.JPY, new CurrencyRateDto(Currency.JPY, new BigDecimal("172.0715108689")));
        data.put(Currency.USD, new CurrencyRateDto(Currency.USD, new BigDecimal("1.1682104675")));
        mockResponse.setData(data);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                eq(null),
                eq(CurrencyApiResponse.class)
        )).thenReturn(ResponseEntity.ok(mockResponse));
    }

    @Test
    @SneakyThrows
    void getAccountsReturnsAccountsForExistingClient() {
        // given - when - then
        mockMvc.perform(get("/api/clients/%s/accounts".formatted(client.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithNegativeAmount() {
        // given
        FundTransferDto dto = FundTransferDto.builder()
                .fromAccountId(account1.getId())
                .toAccountId(account1.getId())
                .amount(new BigDecimal("-10.00"))
                .currency(Currency.EUR)
                .build();

        // when - then
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Transfer amount must be positive"));
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithSameAccountIds() {
        // given
        FundTransferDto dto = FundTransferDto.builder()
                .fromAccountId(account1.getId())
                .toAccountId(account1.getId())
                .amount(new BigDecimal("10.00"))
                .currency(Currency.EUR)
                .build();

        // when - then
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot transfer to the same account"));
    }

    private static Stream<Arguments> transferFundsSucceedsWithCurrencyConversion() {
        return Stream.of(
                Arguments.of(Currency.USD, new BigDecimal("997.66")),
                Arguments.of(Currency.EUR, new BigDecimal("998.00")),
                Arguments.of(Currency.JPY, new BigDecimal("655.86")),
                Arguments.of(Currency.CAD, new BigDecimal("996.80"))
        );
    }

    @MethodSource
    @ParameterizedTest
    @SneakyThrows
    void transferFundsSucceedsWithCurrencyConversion(Currency fromCurrency, BigDecimal expectedBalance) {
        // given
        FundTransferDto dto = FundTransferDto.builder()
                .fromAccountId(account1.getId())
                .toAccountId(account2.getId())
                .amount(new BigDecimal("2.00"))
                .currency(Currency.EUR)
                .build();

        account1.setCurrency(fromCurrency);
        accountRepository.save(account1);

        // when
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        Account updatedAccount1 = accountRepository.findById(account1.getId()).orElseThrow();
        Account updatedAccount2 = accountRepository.findById(account2.getId()).orElseThrow();

        assertEquals(expectedBalance, updatedAccount1.getBalance());
        assertEquals(new BigDecimal("52.00"), updatedAccount2.getBalance());

        var senderTransaction = transactionRepository.findAll().stream()
                .filter(t -> t.getAccount().getId().equals(account1.getId()))
                .findFirst().orElseThrow();

        var receiverTransaction = transactionRepository.findAll().stream()
                .filter(t -> t.getAccount().getId().equals(account2.getId()))
                .findFirst().orElseThrow();

        assertEquals(Currency.EUR, senderTransaction.getCurrency());
        assertEquals(new BigDecimal("-2.00"), senderTransaction.getAmount());

        assertEquals(Currency.EUR, receiverTransaction.getCurrency());
        assertEquals(new BigDecimal("2.00"), receiverTransaction.getAmount());
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWhenTransferCurrencyDoesNotMatchReceiverAccountCurrency() {
        // given
        FundTransferDto dto = FundTransferDto.builder()
                .fromAccountId(account1.getId())
                .toAccountId(account2.getId())
                .amount(new BigDecimal("10.00"))
                .currency(Currency.USD)
                .build();

        // when - then
        mockMvc.perform(post("/api/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Destination account currency does not match the requested target currency"));
    }

    private static Stream<Arguments> transactionPagingArguments() {
        return Stream.of(
                Arguments.of(0, 5, 5),
                Arguments.of(0, 10, 10),
                Arguments.of(5, 5, 5),
                Arguments.of(10, 10, 10),
                Arguments.of(15, 10, 5),
                Arguments.of(20, 1, 0)
        );
    }

    @MethodSource("transactionPagingArguments")
    @SneakyThrows
    @ParameterizedTest
    void getTransactionsReturnsCorrectNumberOfTransactions(int offset, int limit, int expectedCount) {
        // given
        generateTransactions(account1, 20);

        // when - then
        mockMvc.perform(get("/api/accounts/%s/transactions".formatted(account1.getId()))
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedCount));
    }

    private void generateTransactions(Account account, int count) {
        for (int i = 0; i < count; i++) {
            Transaction transaction = Transaction.builder()
                    .account(account)
                    .amount(new BigDecimal("-5.00").add(new BigDecimal(i)))
                    .timestamp(LocalDateTime.now().minusDays(i))
                    .currency(Currency.EUR)
                    .build();
            transactionRepository.save(transaction);
        }
    }

}
