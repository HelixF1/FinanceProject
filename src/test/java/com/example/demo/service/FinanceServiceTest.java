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
import static org.mockito.Mockito.doReturn;
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

    @SuppressWarnings("unchecked")
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
        doReturn(yahooUriSpec).when(yahooFinanceWebClient).get();
        doReturn(yahooHeadersSpec).when(yahooUriSpec).uri(any(String.class));
        doReturn(yahooResponseSpec).when(yahooHeadersSpec).retrieve();
        doReturn(Mono.just(stockResponse)).when(yahooResponseSpec).bodyToMono(String.class);

        // Currency WebClient yapılandırması
        doReturn(currencyUriSpec).when(currencyWebClient).get();
        doReturn(currencyHeadersSpec).when(currencyUriSpec).uri(any(Function.class));
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