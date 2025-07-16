package com.mintos.account_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mintos.account_management.model.Currency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FundTransferDto {

    @Valid
    @NotNull
    private Long fromAccountId;

    @Valid
    @NotNull
    private Long toAccountId;

    @Valid
    @NotNull
    private BigDecimal amount;

    @Valid
    @NotNull
    private Currency currency;

}
