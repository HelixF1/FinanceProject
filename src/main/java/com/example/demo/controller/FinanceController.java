package com.example.demo.controller;

import com.example.demo.model.StockRequest;
import com.example.demo.service.FinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private static final Logger logger = LoggerFactory.getLogger(FinanceController.class);

    @Autowired
    private FinanceService financeService;

    // Döviz kuru endpoint'i
    @GetMapping("/exchange-rate")
    public ResponseEntity<?> getExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return executeFinanceOperation("getExchangeRate", () -> financeService.getExchangeRate(fromCurrency, toCurrency, date));
    }

    // Tek hisse senedi fiyatı endpoint'i
    @GetMapping("/stock-price")
    public ResponseEntity<?> getStockPrice(
            @RequestParam String symbol,
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return executeFinanceOperation("getStockPrice", 
            () -> financeService.getStockPrice(symbol, currency, date));
    }

    // Toplu hisse senedi fiyatı endpoint'i
    @PostMapping("/bulk-stock-prices")
    public ResponseEntity<?> getBulkStockPrices(@RequestBody StockRequest request) {
        return executeFinanceOperation("getBulkStockPrices", 
            () -> financeService.getBulkStockPrices(request));
    }

    @GetMapping("/total-portfolio-value")
    public ResponseEntity<?> getTotalPortfolioValue(
            @RequestParam List<String> symbols,
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return executeFinanceOperation("getTotalPortfolioValue", () -> financeService.calculateTotalPortfolioValue(symbols, currency, date));
    }

    private <T> ResponseEntity<?> executeFinanceOperation(String operationName, Supplier<T> operation) {
        try {
            logger.info("Executing operation: {}", operationName);
            T result = operation.get();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error in {}: ", operationName, e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}