package com.example.demo.service;

import com.example.demo.model.StockRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(FinanceService.class);
    
    @Value("${api.currency.api-key}")
    private String currencyApiKey;
    
    private final WebClient yahooFinanceWebClient;
    private final WebClient currencyWebClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public FinanceService(
            @Qualifier("yahooFinanceWebClient") WebClient yahooFinanceWebClient,
            @Qualifier("currencyWebClient") WebClient currencyWebClient) {
        this.yahooFinanceWebClient = yahooFinanceWebClient;
        this.currencyWebClient = currencyWebClient;
        this.objectMapper = new ObjectMapper();
    }
    
    public double getStockPrice(String symbol, String currency, LocalDate date) {
        try {
            String response = yahooFinanceWebClient
                .get()
                .uri("/" + symbol)
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
            JsonNode json = objectMapper.readTree(response);
            
            if (!json.has("chart") || !json.get("chart").has("result")) {
                throw new RuntimeException("API yanıtı geçersiz format içeriyor");
            }
            
            JsonNode result = json.get("chart").get("result").get(0);
            JsonNode quote = result.get("indicators").get("quote").get(0);
            double price = quote.get("close").get(0).asDouble();
            
            if (!currency.equals("USD")) {
                double exchangeRate = getExchangeRate("USD", currency, date);
                price *= exchangeRate;
            }
            
            return price;
            
        } catch (Exception e) {
            logger.error("Error getting stock price: {}", e.getMessage());
            throw new RuntimeException("Hisse fiyatı alınamadı: " + e.getMessage());
        }
    }
    
    public double getExchangeRate(String fromCurrency, String toCurrency, LocalDate date) {
        try {
            String response = currencyWebClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/latest")
                    .queryParam("apikey", currencyApiKey)
                    .queryParam("base_currency", fromCurrency)
                    .queryParam("currencies", toCurrency)
                    .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
            JsonNode json = objectMapper.readTree(response);
            
            if (!json.has("data") || !json.get("data").has(toCurrency)) {
                throw new RuntimeException("Döviz kuru bulunamadı");
            }
            
            return json.get("data").get(toCurrency).asDouble();
            
        } catch (Exception e) {
            logger.error("Error getting exchange rate: {}", e.getMessage());
            throw new RuntimeException("Döviz kuru alınamadı: " + e.getMessage());
        }
    }

    public Map<String, Double> getBulkStockPrices(StockRequest request) {
        Map<String, Double> prices = new HashMap<>();
        for (String symbol : request.getSymbols()) {
            try {
                double price = getStockPrice(symbol, request.getCurrency(), request.getDate());
                prices.put(symbol, price);
            } catch (Exception e) {
                logger.error("Error getting price for {}: {}", symbol, e.getMessage());
                prices.put(symbol, -1.0);
            }
        }
        return prices;
    }

    public double calculateTotalPortfolioValue(List<String> symbols, String currency, LocalDate date) {
        return symbols.stream()
            .mapToDouble(symbol -> {
                try {
                    return getStockPrice(symbol, currency, date);
                } catch (Exception e) {
                    logger.error("Error calculating value for {}: {}", symbol, e.getMessage());
                    return 0.0;
                }
            })
            .sum();
    }
}

