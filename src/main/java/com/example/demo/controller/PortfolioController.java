package com.example.demo.controller;

import com.example.demo.dto.StockHistoryDTO;
import com.example.demo.model.Portfolio;
import com.example.demo.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> getHistory(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return portfolioService.getPortfolioHistory(userId, startDate, endDate);
    }
    
    @GetMapping("/total-value")
    public Map<LocalDate, Double> getTotalValue(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return portfolioService.getPortfolioTotalValue(userId, startDate, endDate);
    }
    
    @PostMapping("/update-prices")
    public void updatePrices() {
        portfolioService.scheduledPriceUpdate();
    }
    
    @GetMapping("/user-stocks")
    public List<String> getUserStocks(@RequestParam String userId) {
        Portfolio portfolio = portfolioService.findPortfolioByUserId(userId);
        return portfolio.getStocks().stream()
            .map(stock -> stock.getSymbol())
            .toList();
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePortfolio(@RequestParam String userId) {
        try {
            portfolioService.deletePortfolio(userId);
            return ResponseEntity.ok().body("Portfolio başarıyla silindi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 