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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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

        // WebClient.Builder mock'ları
        WebClient.RequestHeadersUriSpec<?> uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // Yahoo Finance için mock yapılandırması
        lenient().when(yahooFinanceWebClient.get()).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(stockResponse));

        // Currency API için mock yapılandırması
        lenient().when(currencyWebClient.get()).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(currencyResponse));

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