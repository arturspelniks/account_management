package com.mintos.account_management.service;

import com.mintos.account_management.model.Account;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.repository.AccountRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountService accountService;

    @Test
    @SneakyThrows
    void transferFundsSucceedsWithValidInput() {
        Account fromAccount = Account.builder()
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();
        
        Account toAccount = Account.builder()
                .balance(new BigDecimal("50.00"))
                .currency(Currency.USD)
                .build();

        Mockito.when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        accountService.transferFunds(1L, 2L, new BigDecimal("10.00"), Currency.USD);

        assertEquals(new BigDecimal("90.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("60.00"), toAccount.getBalance());
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithNegativeAmount() {
        // given - when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transferFunds(1L, 2L, new BigDecimal("-10.00"), Currency.USD);
        });
        assertEquals("Transfer amount must be positive", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithSameAccountIds() {
        // given - when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transferFunds(1L, 1L, new BigDecimal("10.00"), Currency.USD);
        });
        assertEquals("Cannot transfer to the same account", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithInsufficientFunds() {
        // given
        Account fromAccount = Account.builder()
                .balance(new BigDecimal("5.00"))
                .currency(Currency.USD)
                .build();
        Account toAccount = Account.builder()
                .balance(new BigDecimal("50.00"))
                .currency(Currency.USD)
                .build();

        Mockito.when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transferFunds(1L, 2L, new BigDecimal("10.00"), Currency.USD);
        });
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void transferFundsFailsWithCurrencyMismatch() {
        // given
        Account fromAccount = Account.builder()
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();
        Account toAccount = Account.builder()
                .balance(new BigDecimal("50.00"))
                .currency(Currency.EUR)
                .build();

        Mockito.when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        // when - then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.transferFunds(1L, 2L, new BigDecimal("10.00"), Currency.USD);
        });
        assertEquals("Destination account currency does not match the requested target currency", exception.getMessage());
    }

    @Test
    @SneakyThrows
    void transferFundsSucceedsWithCurrencyConversion() {
        // given
        Account fromAccount = Account.builder()
                .balance(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .build();
        Account toAccount = Account.builder()
                .balance(new BigDecimal("50.00"))
                .currency(Currency.EUR)
                .build();

        Mockito.when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        Mockito.when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        Mockito.when(currencyService.getExchangeRate(Currency.EUR, Currency.USD)).thenReturn(new BigDecimal("0.85"));

        // when
        accountService.transferFunds(1L, 2L, new BigDecimal("10.00"), Currency.EUR);

        // then
        assertEquals(new BigDecimal("91.50"), fromAccount.getBalance().setScale(2));
        assertEquals(new BigDecimal("60.00"), toAccount.getBalance().setScale(2));
    }
}
