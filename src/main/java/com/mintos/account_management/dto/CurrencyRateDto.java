package com.mintos.account_management.dto;

import com.mintos.account_management.model.Currency;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateDto {

    private Currency code;
    private BigDecimal value;

}
