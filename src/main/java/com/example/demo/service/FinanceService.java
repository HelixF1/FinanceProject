package com.example.demo.service;

import com.example.demo.model.StockRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class FinanceService {
    
    // Döviz çevirme işlemi
    public Double getExchangeRate(String fromCurrency, String toCurrency, LocalDate date) {
        // Burada gerçek bir API'ye bağlanılacak (örn: Exchange Rates API)
        // Şimdilik örnek veri dönüyoruz
        if (fromCurrency.equals("USD") && toCurrency.equals("EUR")) {
            return 0.85; // 1 USD = 0.85 EUR
        }
        return 1.0;
    }

    // Tek hisse senedi fiyatı alma
    public Double getStockPrice(String symbol, String currency, LocalDate date) {
        // Burada gerçek bir API'ye bağlanılacak (örn: Yahoo Finance API)
        // Şimdilik örnek veriler dönüyoruz
        Map<String, Double> dummyPrices = new HashMap<>();
        dummyPrices.put("AAPL", 150.0);
        dummyPrices.put("GOOGL", 2800.0);
        dummyPrices.put("MSFT", 280.0);
        
        return dummyPrices.getOrDefault(symbol, 0.0);
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
} 