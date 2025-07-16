package com.mintos.account_management.service;

import com.mintos.account_management.dto.TransactionDto;
import com.mintos.account_management.mapper.TransactionMapper;
import com.mintos.account_management.model.Account;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.model.Transaction;
import com.mintos.account_management.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public List<TransactionDto> getTransactions(Long accountId, int offset, int limit) {
        List<Transaction> transactions = transactionRepository.findTransactionsWithOffsetAndLimit(accountId, offset, limit);
        return transactions.stream()
                .map(transactionMapper::mapTransactionToTransactionDto)
                .toList();
    }

    public void recordTransaction(Long fromAccountId, Long toAccountId, BigDecimal amount, Account fromAccount,
                                  Account toAccount, Currency currency) {
        log.debug("Recording transaction from account {} to account {} with amount {}", fromAccountId, toAccountId, amount);
        Instant instant = Instant.now();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        Transaction debitTransaction = Transaction.builder()
                .account(fromAccount)
                .amount(amount.negate())
                .timestamp(localDateTime)
                .currency(currency)
                .description("Transfer to account " + toAccountId)
                .build();

        Transaction creditTransaction = Transaction.builder()
                .account(toAccount)
                .amount(amount)
                .timestamp(localDateTime)
                .currency(currency)
                .description("Transfer from account " + fromAccountId)
                .build();

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);
    }

}