package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExchangeRateConfig {
    @Value("${exchangerate.api.key}")
    private String apiKey;
    
    @Bean(name = "exchangeRateWebClient")
    public WebClient exchangeRateWebClient() {
        return WebClient.builder()
                .baseUrl("https://v6.exchangerate-api.com/v6")
                .build();
    }

    @Bean(name = "stockWebClient")
    public WebClient stockWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.alphavantage.co")
                .build();
    }
} 