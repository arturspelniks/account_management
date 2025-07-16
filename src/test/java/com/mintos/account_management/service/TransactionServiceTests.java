package com.mintos.account_management.service;

import com.mintos.account_management.dto.TransactionDto;
import com.mintos.account_management.mapper.TransactionMapper;
import com.mintos.account_management.model.Account;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.Transaction;
import com.mintos.account_management.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private Account fromAccount;

    @Mock
    private Account toAccount;

    @Test
    void getTransactionsReturnsEmptyListWhenNoTransactionsFound() {
        // given
        Long accountId = 1L;
        when(transactionRepository.findTransactionsWithOffsetAndLimit(eq(accountId), eq(0), eq(10)))
                .thenReturn(List.of());

        // when
        List<TransactionDto> result = transactionService.getTransactions(accountId, 0, 10);

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void getTransactionsReturnsMappedTransactions() {
        // given
        Long accountId = 1L;
        Transaction transaction = mock(Transaction.class);
        TransactionDto transactionDto = mock(TransactionDto.class);

        when(transactionRepository.findTransactionsWithOffsetAndLimit(accountId, 0, 10))
                .thenReturn(List.of(transaction));
        when(transactionMapper.mapTransactionToTransactionDto(transaction))
                .thenReturn(transactionDto);

        // when
        List<TransactionDto> result = transactionService.getTransactions(accountId, 0, 10);

        // then
        assertEquals(1, result.size());
        assertEquals(transactionDto, result.get(0));
    }

    @Test
    void getTransactionsCalculatesCorrectOffsetAndLimit() {
        // given
        Long accountId = 1L;
        when(transactionRepository.findTransactionsWithOffsetAndLimit(eq(accountId), eq(20), eq(10)))
                .thenReturn(List.of());

        // when
        transactionService.getTransactions(accountId, 20, 10);

        // then
        verify(transactionRepository).findTransactionsWithOffsetAndLimit(accountId, 20, 10);
    }

    @Test
    void recordTransactionSavesDebitAndCreditTransactions() {
        // given
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.USD;

        // when
        transactionService.recordTransaction(fromAccountId, toAccountId, amount, fromAccount, toAccount, currency);

        // then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(transactionCaptor.capture());

        List<Transaction> savedTransactions = transactionCaptor.getAllValues();
        Transaction debitTransaction = savedTransactions.get(0);
        Transaction creditTransaction = savedTransactions.get(1);

        assertEquals(fromAccount, debitTransaction.getAccount());
        assertEquals(amount.negate(), debitTransaction.getAmount());
        assertEquals(currency, debitTransaction.getCurrency());
        assertEquals("Transfer to account " + toAccountId, debitTransaction.getDescription());

        assertEquals(toAccount, creditTransaction.getAccount());
        assertEquals(amount, creditTransaction.getAmount());
        assertEquals(currency, creditTransaction.getCurrency());
        assertEquals("Transfer from account " + fromAccountId, creditTransaction.getDescription());
    }

    @Test
    void recordTransactionSetsCurrentTimestampOnBothTransactions() {
        // given
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        BigDecimal amount = new BigDecimal("50.00");
        Currency currency = Currency.EUR;

        // when
        transactionService.recordTransaction(fromAccountId, toAccountId, amount, fromAccount, toAccount, currency);

        // then
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(2)).save(transactionCaptor.capture());

        List<Transaction> savedTransactions = transactionCaptor.getAllValues();
        assertNotNull(savedTransactions.get(0).getTimestamp());
        assertNotNull(savedTransactions.get(1).getTimestamp());
        assertEquals(savedTransactions.get(0).getTimestamp(), savedTransactions.get(1).getTimestamp());
    }

}