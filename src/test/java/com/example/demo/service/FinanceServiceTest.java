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
import static org.mockito.Mockito.doReturn;

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

        // WebClient mock'ları
        WebClient.RequestHeadersUriSpec<?> uriSpec = WebClient.builder().build().get();
        WebClient.RequestHeadersSpec<?> headersSpec = uriSpec.uri("/dummy");
        WebClient.ResponseSpec responseSpec = headersSpec.retrieve();

        // Yahoo Finance için mock
        doReturn(Mono.just(stockResponse)).when(yahooFinanceWebClient).get().uri(any(String.class)).retrieve().bodyToMono(String.class);

        // Currency API için mock
        doReturn(Mono.just(currencyResponse)).when(currencyWebClient).get().uri(any(String.class)).retrieve().bodyToMono(String.class);

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