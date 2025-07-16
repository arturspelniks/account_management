package com.mintos.account_management.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "currency-api")
public class CurrencyApiProperties {

    private String url;
    private String apiKey;

}
