package com.mintos.account_management.dto;

import com.mintos.account_management.model.Currency;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private BigDecimal balance;
    private Currency currency;

}
