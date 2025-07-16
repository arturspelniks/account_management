package com.mintos.account_management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mintos.account_management.model.Currency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDto {

    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private Currency currency;

}
