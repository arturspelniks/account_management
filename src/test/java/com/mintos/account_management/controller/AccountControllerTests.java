package com.mintos.account_management.controller;

import com.mintos.account_management.dto.AccountDto;
import com.mintos.account_management.dto.FundTransferDto;
import com.mintos.account_management.dto.TransactionDto;
import com.mintos.account_management.model.Currency;
import com.mintos.account_management.service.AccountService;
import com.mintos.account_management.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountControllerTests {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountController controller;

    @Test
    void getAccounts_callsServiceAndReturnsResult() {
        // given
        List<AccountDto> expected = List.of(new AccountDto());
        when(accountService.getAccountsByClientId(1L)).thenReturn(expected);

        // when
        List<AccountDto> result = controller.getAccounts(1L);

        // then
        assertThat(result).isSameAs(expected);
        verify(accountService).getAccountsByClientId(1L);
    }

    @Test
    void getTransactions_callsServiceAndReturnsResult() {
        // given
        List<TransactionDto> expected = List.of(new TransactionDto());
        when(transactionService.getTransactions(2L, 0, 10)).thenReturn(expected);

        // when
        List<TransactionDto> result = controller.getTransactions(2L, 0, 10);

        // then
        assertThat(result).isSameAs(expected);
        verify(transactionService).getTransactions(2L, 0, 10);
    }

    @Test
    void transferFunds_callsServiceWithDtoFields() {
        // given
        FundTransferDto dto = FundTransferDto.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(BigDecimal.TEN)
                .currency(Currency.EUR)
                .build();

        // when
        var response = controller.transferFunds(dto);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(accountService).transferFunds(1L, 2L, BigDecimal.TEN, Currency.EUR);
    }

}