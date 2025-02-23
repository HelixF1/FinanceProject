package com.example.demo.controller;

import com.example.demo.model.StockRequest;
import com.example.demo.service.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    // Döviz kuru endpoint'i
    @GetMapping("/exchange-rate")
    public Double getExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return financeService.getExchangeRate(fromCurrency, toCurrency, date);
    }

    // Tek hisse senedi fiyatı endpoint'i
    @GetMapping("/stock-price")
    public Double getStockPrice(
            @RequestParam String symbol,
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return financeService.getStockPrice(symbol, currency, date);
    }

    // Toplu hisse senedi fiyatı endpoint'i
    @PostMapping("/bulk-stock-prices")
    public Map<String, Double> getBulkStockPrices(@RequestBody StockRequest request) {
        return financeService.getBulkStockPrices(request);
    }
}