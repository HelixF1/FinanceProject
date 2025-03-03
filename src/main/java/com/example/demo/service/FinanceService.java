package com.example.demo.service;

import com.example.demo.model.StockRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Set;
import java.time.ZoneId;

@Service
public class FinanceService {
    
    private static final Logger logger = LoggerFactory.getLogger(FinanceService.class);
    private final Random random = new Random();
    @Autowired
    @Qualifier("exchangeRateWebClient")
    private WebClient exchangeRateWebClient;
    
    @Autowired
    @Qualifier("stockWebClient")
    private WebClient stockWebClient;
    
    @Value("${exchangerate.api.key}")
    private String apiKey;
    
    @Value("${alphavantage.api.key}")
    private String stockApiKey;
    
    private Map<String, Double> priceCache = new ConcurrentHashMap<>();
    
    // Döviz çevirme işlemi
    public Double getExchangeRate(String fromCurrency, String toCurrency, LocalDate date) {
        String url = String.format("/%s/latest/%s", 
            apiKey.trim(),
            fromCurrency.toUpperCase());
            
        logger.info("Calling exchange rate API with URL: {}", url);
        
        try {
            String response = exchangeRateWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            logger.info("Raw API Response: {}", response);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            
            if (json.has("conversion_rates")) {
                Double rate = json.get("conversion_rates").get(toCurrency.toUpperCase()).asDouble();
                logger.info("Exchange rate result: {}", rate);
                return rate;
            } else {
                throw new RuntimeException("Invalid API response: " + response);
            }
        } catch (Exception e) {
            logger.error("Error getting exchange rate: ", e);
            throw new RuntimeException("Error getting exchange rate: " + e.getMessage());
        }
    }

    
    public double getStockPrice(String symbol, String currency, LocalDate date) {
        try {
            String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&period1=%d&period2=%d",
                symbol,
                date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                date.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toEpochSecond());
            
            logger.info("Calling Yahoo Finance API with URL: {}", url);
            
            String response = stockWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            
            if (!json.has("chart") || !json.get("chart").has("result")) {
                throw new RuntimeException("API yanıtı geçersiz format içeriyor");
            }
            
            JsonNode result = json.get("chart").get("result").get(0);
            if (!result.has("indicators") || !result.get("indicators").has("quote")) {
                throw new RuntimeException("Hisse fiyatı API yanıtında bulunamadı");
            }
            
            JsonNode quote = result.get("indicators").get("quote").get(0);
            if (!quote.has("close") || quote.get("close").size() == 0) {
                throw new RuntimeException("Hisse fiyatı bulunamadı");
            }
            
            double price = quote.get("close").get(0).asDouble();
            
            // Para birimi dönüşümü
            if (!currency.equalsIgnoreCase("USD")) {
                try {
                    Double exchangeRate = getExchangeRate("USD", currency, date);
                    price *= exchangeRate;
                    logger.info("Converted price from USD to {}: {} * {} = {}", 
                        currency, price/exchangeRate, exchangeRate, price);
                } catch (Exception e) {
                    logger.error("Error converting currency: {}", e.getMessage());
                    throw new RuntimeException("Para birimi dönüşümü yapılamadı: " + e.getMessage());
                }
            }
            
            return Math.round(price * 100.0) / 100.0; // 2 ondalık basamağa yuvarla
            
        } catch (Exception e) {
            logger.error("Error in getStockPrice: {}", e.getMessage());
            throw new RuntimeException("Hisse fiyatı alınamadı: " + e.getMessage());
        }
    }

    // Toplu hisse senedi fiyatı alma
    public Map<String, Double> getBulkStockPrices(StockRequest request) {
        Map<String, Double> results = new HashMap<>();
        for (String symbol : request.getStockSymbols()) {
            try {
                Double price = getStockPrice(symbol, request.getCurrency(), request.getDate());
                results.put(symbol, price);
            } catch (Exception e) {
                logger.error("Error getting price for {}: {}", symbol, e.getMessage());
                throw new RuntimeException(symbol + " için fiyat alınamadı: " + e.getMessage());
            }
        }
        return results;
    }

    public Map<String, Object> calculateTotalPortfolioValue(List<String> symbols, String currency, LocalDate date) {
        logger.info("Calculating total value for {} stocks in {}", symbols.size(), currency);
        
        // Boş ve geçersiz sembolleri filtrele
        symbols = symbols.stream()
                .filter(symbol -> symbol != null && !symbol.trim().isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        Map<String, Double> values = new HashMap<>();
        Double total = 0.0;
        
        for (String symbol : symbols) {
            try {
                Double price = getStockPrice(symbol, currency, date);
                values.put(symbol, price);
                total += price;
            } catch (Exception e) {
                logger.error("Error getting price for {}: {}", symbol, e.getMessage());
                throw new RuntimeException(symbol + " için fiyat alınamadı: " + e.getMessage());
            }
        }
        
        return Map.of(
            "symbols", symbols,
            "currency", currency,
            "values", values,
            "totalValue", total
        );
    }
} 