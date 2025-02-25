package com.example.demo.controller;

import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    
    @Autowired
    private PortfolioService portfolioService;
    
    @PostMapping("/create")
    public Portfolio createPortfolio(@RequestParam String userId) {
        return portfolioService.createPortfolio(userId);
    }
    
    @PostMapping("/add-stock")
    public void addStock(
            @RequestParam String userId,
            @RequestParam String symbol,
            @RequestParam int quantity) {
        portfolioService.addStockToPortfolio(userId, symbol, quantity);
    }
    
    @GetMapping("/history")
    public Map<LocalDate, Map<String, Map<String, Object>>> getHistory(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return portfolioService.getPortfolioHistory(userId, startDate, endDate);
    }
    
    @GetMapping("/total-history")
    public Map<LocalDate, Double> getPortfolioTotalHistory(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return portfolioService.getPortfolioTotalHistory(userId, startDate, endDate);
    }
    
    @PostMapping("/update-prices")
    public void updatePrices() {
        portfolioService.updateDailyPrices();
    }
    
    @GetMapping("/stock-detail-history")
    public Map<LocalDate, Map<String, Object>> getStockDetailHistory(
            @RequestParam String userId,
            @RequestParam String symbol) {
        return portfolioService.getStockDetailHistory(userId, symbol);
    }
    
    @GetMapping("/user-stocks")
    public List<String> getUserStocks(@RequestParam String userId) {
        Portfolio portfolio = portfolioService.findFirstByUserId(userId);
        return portfolio.getStocks().stream()
            .map(PortfolioStock::getSymbol)
            .collect(Collectors.toList());
    }
    
    @GetMapping("/total-history-with-details")
    public Map<LocalDate, Map<String, Object>> getPortfolioTotalHistoryWithDetails(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return portfolioService.getPortfolioTotalHistoryWithDetails(userId, startDate, endDate);
    }
} 