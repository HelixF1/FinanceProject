package com.example.demo.service;

import com.example.demo.model.StockRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;  // Şimdilik test için rastgele fiyatlar üreteceğiz
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
            apiKey.trim(),  // Boşlukları temizle
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
            
            if (json.has("conversion_rates")) {  // API yanıt formatı değişti
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

    // Şimdilik test için rastgele fiyatlar üretiyoruz
    public Double getStockPrice(String symbol, String currency, LocalDate date) {
        // Cache key oluştur
        String cacheKey = symbol + "_" + currency + "_" + date;
        
        // Cache'de varsa, cache'den döndür
        if (priceCache.containsKey(cacheKey)) {
            logger.info("Returning cached price for {}", cacheKey);
            return priceCache.get(cacheKey);
        }

        try {
            String url = String.format("/query?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                symbol.toUpperCase(), stockApiKey);
                
            logger.info("Calling stock API with URL: {}", url);
            
            String response = stockWebClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            
            if (json.has("Global Quote")) {
                Double priceInUSD = json.get("Global Quote").get("05. price").asDouble();
                Double price = priceInUSD;
                
                if (!currency.equalsIgnoreCase("USD")) {
                    Double exchangeRate = getExchangeRate("USD", currency, date);
                    price = priceInUSD * exchangeRate;
                }
                
                // Sonucu cache'e kaydet
                priceCache.put(cacheKey, price);
                return price;
            } else {
                // API limit aşıldıysa test verilerini kullan
                if (response.contains("rate limit")) {
                    Double testPrice = getTestPrice(symbol, currency);
                    priceCache.put(cacheKey, testPrice);
                    return testPrice;
                }
                throw new RuntimeException("Invalid API response: " + response);
            }
        } catch (Exception e) {
            logger.error("Error getting stock price: ", e);
            // API limit aşıldığında test verileri döndür
            if (e.getMessage().contains("rate limit")) {
                Double testPrice = getTestPrice(symbol, currency);
                priceCache.put(cacheKey, testPrice);
                return testPrice;
            }
            throw new RuntimeException("Error getting stock price: " + e.getMessage());
        }
    }

    // Test verileri için yardımcı metod
    private Double getTestPrice(String symbol, String currency) {
        Map<String, Double> testPrices = Map.of(
            "AAPL", 245.55,
            "GOOGL", 179.66,
            "MSFT", 425.22,
            "TSLA", 202.64,
            "AMZN", 178.35,
            "META", 484.03,
            "NVDA", 788.17,
            "JPM", 183.99,
            "V", 275.96,
            "WMT", 175.56
        );
        
        Double basePrice = testPrices.getOrDefault(symbol, 100.0);
        
        if (!currency.equalsIgnoreCase("USD")) {
            try {
                Double exchangeRate = getExchangeRate("USD", currency, LocalDate.now());
                return basePrice * exchangeRate;
            } catch (Exception e) {
                logger.error("Error converting test price: ", e);
                return basePrice;
            }
        }
        
        return basePrice;
    }

    // Toplu hisse senedi fiyatı alma
    public Map<String, Double> getBulkStockPrices(StockRequest request) {
        Map<String, Double> results = new HashMap<>();
        for (String symbol : request.getStockSymbols()) {
            Double price = getStockPrice(symbol, request.getCurrency(), request.getDate());
            results.put(symbol, price);
        }
        return results;
    }

    public Map<String, Object> calculateTotalPortfolioValue(List<String> symbols, String currency, LocalDate date) {
        logger.info("Calculating total value for {} stocks in {}", symbols.size(), currency);
        
        // Geçerli hisse kodları listesi
        Set<String> validSymbols = Set.of(
            "AAPL", "GOOGL", "MSFT", "TSLA", "AMZN", 
            "META", "NVDA", "JPM", "V", "WMT"
        );
        
        // Boş ve geçersiz sembolleri filtrele
        symbols = symbols.stream()
                .filter(symbol -> symbol != null && !symbol.trim().isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(validSymbols::contains)
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
                Double testPrice = getTestPrice(symbol, currency);
                values.put(symbol, testPrice);
                total += testPrice;
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