package com.mintos.account_management.dto;

import com.mintos.account_management.model.Currency;
import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyApiResponse {

    private Map<Currency, CurrencyRateDto> data;

}
