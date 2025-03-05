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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private WebClient yahooFinanceWebClient;
    
    @Mock
    private WebClient currencyWebClient;

    private FinanceService financeService;

    @BeforeEach
    void setUp() {
        // Mock yanıtları
        String stockResponse = """
            {
                "chart": {
                    "result": [{
                        "meta": {"regularMarketPrice": 150.0},
                        "timestamp": [1234567890],
                        "indicators": {
                            "quote": [{
                                "close": [150.0]
                            }]
                        }
                    }]
                }
            }""";

        String currencyResponse = """
            {
                "data": {
                    "EUR": 0.85
                }
            }""";

        // Yahoo Finance WebClient yapılandırması
        WebClient.RequestHeadersUriSpec<?> yahooSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> yahooHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec yahooResponseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(yahooSpec).when(yahooFinanceWebClient).get();
        doReturn(yahooHeadersSpec).when(yahooSpec).uri(any(String.class));
        doReturn(yahooResponseSpec).when(yahooHeadersSpec).retrieve();
        doReturn(Mono.just(stockResponse)).when(yahooResponseSpec).bodyToMono(String.class);

        // Currency WebClient yapılandırması
        WebClient.RequestHeadersUriSpec<?> currencySpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> currencyHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec currencyResponseSpec = mock(WebClient.ResponseSpec.class);

        doReturn(currencySpec).when(currencyWebClient).get();
        doReturn(currencyHeadersSpec).when(currencySpec).uri(any(String.class));
        doReturn(currencyResponseSpec).when(currencyHeadersSpec).retrieve();
        doReturn(Mono.just(currencyResponse)).when(currencyResponseSpec).bodyToMono(String.class);

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
        
        // Doğrulama
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
        
        // Doğrulama
        assertTrue(rate > 0);
        assertEquals(0.85, rate);
    }
}