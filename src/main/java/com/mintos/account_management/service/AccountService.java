package com.mintos.account_management.service;

import com.mintos.account_management.dto.AccountDto;
import com.mintos.account_management.mapper.AccountMapper;
import com.mintos.account_management.model.Account;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId)
                .stream()
                .map(accountMapper::mapAccountToAccountDto)
                .toList();
    }

    @Transactional
    public void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount, Currency currency) {
        validateTransferInputs(fromAccountId, toAccountId, amount);

        Account fromAccount = getAccountForUpdate(fromAccountId, "Source account not found");
        Account toAccount = getAccountForUpdate(toAccountId, "Destination account not found");

        validateCurrencyMatch(toAccount, currency);

        BigDecimal convertedAmount = convertAmountIfNeeded(amount, fromAccount.getCurrency(), currency);

        ensureSufficientFunds(fromAccount, convertedAmount);

        updateBalances(fromAccount, toAccount, convertedAmount, amount);

        transactionService.recordTransaction(fromAccountId, toAccountId, amount, fromAccount, toAccount, currency);
    }

    private void validateTransferInputs(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
    }

    private Account getAccountForUpdate(Long accountId, String errorMessage) {
        return accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new IllegalArgumentException(errorMessage));
    }

    private void validateCurrencyMatch(Account toAccount, Currency currency) {
        if (!toAccount.getCurrency().equals(currency)) {
            throw new IllegalArgumentException("Destination account currency does not match the requested target currency");
        }
    }

    private BigDecimal convertAmountIfNeeded(BigDecimal amount, Currency fromCurrency, Currency targetCurrency) {
        if (!fromCurrency.equals(targetCurrency)) {
            BigDecimal rate = currencyService.getExchangeRate(targetCurrency, fromCurrency);
            return amount.multiply(rate);
        }
        return amount;
    }

    private void ensureSufficientFunds(Account fromAccount, BigDecimal convertedAmount) {
        if (fromAccount.getBalance().compareTo(convertedAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
    }

    private void updateBalances(Account fromAccount, Account toAccount, BigDecimal convertedAmount, BigDecimal originalAmount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(convertedAmount));
        toAccount.setBalance(toAccount.getBalance().add(originalAmount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}