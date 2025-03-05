package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(16 * 1024 * 1024))
                .build());
    }

    @Bean
    public WebClient yahooFinanceWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://query1.finance.yahoo.com/v8/finance/chart")
            .build();
    }

    @Bean
    public WebClient currencyWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://api.freecurrencyapi.com/v1")
            .build();
    }
} 