package com.mintos.account_management.controller;

import com.mintos.account_management.dto.AccountDto;
import com.mintos.account_management.dto.FundTransferDto;
import com.mintos.account_management.dto.TransactionDto;
import com.mintos.account_management.service.AccountService;
import com.mintos.account_management.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/clients/{clientId}/accounts")
    public List<AccountDto> getAccounts(@PathVariable Long clientId) {
        return accountService.getAccountsByClientId(clientId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionDto> getTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") @Min(0) int offset,
            @RequestParam(defaultValue = "25") @Min(1) int limit) {
        return transactionService.getTransactions(accountId, offset, limit);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<Void> transferFunds(@Valid @RequestBody FundTransferDto fundTransferDto) {
        accountService.transferFunds(fundTransferDto.getFromAccountId(), fundTransferDto.getToAccountId(),
                fundTransferDto.getAmount(), fundTransferDto.getCurrency());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}