package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private WebClient yahooFinanceWebClient;
    
    @Mock
    private WebClient currencyWebClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec responseSpec;

    private FinanceService financeService;

    @BeforeEach
    void setUp() {
        // Mock response for stock price
        String stockResponse = """
            {
                "chart": {
                    "result": [{
                        "indicators": {
                            "quote": [{
                                "close": [150.0]
                            }]
                        }
                    }]
                }
            }
            """;
            
        // Mock response for exchange rate
        String rateResponse = """
            {
                "data": {
                    "EUR": 0.85
                }
            }
            """;

        // Setup WebClient mocks
        when(yahooFinanceWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(currencyWebClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Different responses for different clients
        when(responseSpec.bodyToMono(String.class))
            .thenReturn(Mono.just(stockResponse))  // First call (stock price)
            .thenReturn(Mono.just(rateResponse));  // Second call (exchange rate)

        financeService = new FinanceService(yahooFinanceWebClient, currencyWebClient);
    }

    @Test
    void getStockPrice_ShouldReturnPrice() {
        // Test verisi
        String symbol = "AAPL";
        String currency = "USD";
        LocalDate date = LocalDate.now();

        // Test
        double price = financeService.getStockPrice(symbol, currency, date);
        
        assertTrue(price > 0);
        assertEquals(150.0, price);
    }

    @Test
    void getExchangeRate_ShouldReturnRate() {
        // Test verisi
        String fromCurrency = "USD";
        String toCurrency = "EUR";
        LocalDate date = LocalDate.now();

        // Test
        double rate = financeService.getExchangeRate(fromCurrency, toCurrency, date);
        
        assertTrue(rate > 0);
        assertEquals(0.85, rate);
    }
}