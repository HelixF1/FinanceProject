package com.example.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FinanceServiceTest {

    @Mock
    private WebClient yahooFinanceWebClient;
    
    @Mock
    private WebClient currencyWebClient;
    
    @Mock
    private WebClient.RequestHeadersUriSpec<?> yahooUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec<?> yahooHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec yahooResponseSpec;
    
    @Mock
    private WebClient.RequestHeadersUriSpec<?> currencyUriSpec;
    
    @Mock
    private WebClient.RequestHeadersSpec<?> currencyHeadersSpec;
    
    @Mock
    private WebClient.ResponseSpec currencyResponseSpec;

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
        lenient().when(yahooFinanceWebClient.get()).thenReturn(yahooUriSpec);
        lenient().when(yahooUriSpec.uri(any(String.class))).thenReturn(yahooHeadersSpec);
        lenient().when(yahooHeadersSpec.retrieve()).thenReturn(yahooResponseSpec);
        lenient().when(yahooResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(stockResponse));

        // Currency WebClient yapılandırması
        lenient().when(currencyWebClient.get()).thenReturn(currencyUriSpec);
        lenient().when(currencyUriSpec.uri(any(Function.class))).thenReturn(currencyHeadersSpec);
        lenient().when(currencyHeadersSpec.retrieve()).thenReturn(currencyResponseSpec);
        lenient().when(currencyResponseSpec.bodyToMono(String.class)).thenReturn(Mono.just(currencyResponse));

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
        assertEquals(0.85, rate);
    }
}