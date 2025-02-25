package com.example.demo.service;

import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.model.StockHistory;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.StockHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PortfolioService {
    
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private StockHistoryRepository stockHistoryRepository;
    
    @Autowired
    private FinanceService financeService;
    
    public Portfolio createPortfolio(String userId) {
        // findByUserId yerine findFirstByUserId kullan
        Optional<Portfolio> existingPortfolio = portfolioRepository.findFirstByUserId(userId);
        if (existingPortfolio.isPresent()) {
            throw new RuntimeException("Portfolio already exists for user: " + userId);
        }
        
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setStocks(new ArrayList<>());
        return portfolioRepository.save(portfolio);
    }
    
    public void addStockToPortfolio(String userId, String symbol, int quantity) {
        Portfolio portfolio = findPortfolioByUserId(userId);

        Optional<PortfolioStock> existingStock = portfolio.getStocks().stream()
            .filter(s -> s.getSymbol().equals(symbol))
            .findFirst();

        if (existingStock.isPresent()) {
            existingStock.get().setQuantity(existingStock.get().getQuantity() + quantity);
        } else {
            PortfolioStock stock = new PortfolioStock();
            stock.setPortfolio(portfolio);
            stock.setSymbol(symbol);
            stock.setQuantity(quantity);
            stock.setPurchaseDate(LocalDate.now());
            portfolio.getStocks().add(stock);
        }
        
        portfolioRepository.save(portfolio);
        updateStockPrice(symbol, LocalDate.now());
    }
    
    @Scheduled(cron = "0 0 18 * * *")
    public void updateDailyPrices() {
        logger.info("Updating daily prices for all portfolios");
        LocalDate today = LocalDate.now();
        
        portfolioRepository.findAll().stream()
            .flatMap(portfolio -> portfolio.getStocks().stream())
            .map(PortfolioStock::getSymbol)
            .distinct()
            .forEach(symbol -> updateStockPrice(symbol, today));
    }
    
    public Map<LocalDate, Map<String, Map<String, Object>>> getPortfolioHistory(String userId, LocalDate startDate, LocalDate endDate) {
        logger.info("Getting history for user {} from {} to {}", userId, startDate, endDate);
        
        Portfolio portfolio = portfolioRepository.findFirstByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found"));
            
        Map<String, Integer> quantities = new HashMap<>();
        for (PortfolioStock stock : portfolio.getStocks()) {
            quantities.put(stock.getSymbol(), stock.getQuantity());
        }
        
        List<StockHistory> history = stockHistoryRepository.findByDateBetweenAndSymbolInOrderByDateAsc(
            startDate, endDate, new ArrayList<>(quantities.keySet()));
        
        Map<LocalDate, Map<String, Map<String, Object>>> result = new TreeMap<>();
        
        for (StockHistory sh : history) {
            Map<String, Object> stockDetails = new HashMap<>();
            int quantity = quantities.get(sh.getSymbol());
            double pricePerShare = sh.getPrice();
            double totalValue = pricePerShare * quantity;
            
            stockDetails.put("pricePerShare", pricePerShare);
            stockDetails.put("quantity", quantity);
            stockDetails.put("totalValue", totalValue);
            
            result.computeIfAbsent(sh.getDate(), k -> new HashMap<>())
                .put(sh.getSymbol(), stockDetails);
        }
        
        return result;
    }
    
    public Map<LocalDate, Double> getPortfolioTotalHistory(String userId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Map<String, Map<String, Object>>> history = getPortfolioHistory(userId, startDate, endDate);
        
        return history.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().values().stream()
                    .mapToDouble(details -> (double) details.get("totalValue"))
                    .sum()
            ));
    }

    public List<String> getUserStocks(String userId) {
        Portfolio portfolio = portfolioRepository.findFirstByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        
        return portfolio.getStocks().stream()
            .map(PortfolioStock::getSymbol)
            .collect(Collectors.toList());
    }

    public Map<LocalDate, Map<String, Object>> getStockDetailHistory(String userId, String symbol) {
        Portfolio portfolio = findPortfolioByUserId(userId);
        
        PortfolioStock stock = portfolio.getStocks().stream()
            .filter(s -> s.getSymbol().equals(symbol))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Stock not found in portfolio"));
        
        return stockHistoryRepository.findBySymbolOrderByDateAsc(symbol).stream()
            .collect(Collectors.toMap(
                StockHistory::getDate,
                sh -> Map.of(
                    "price", sh.getPrice(),
                    "quantity", stock.getQuantity()
                ),
                (a, b) -> a,
                TreeMap::new
            ));
    }

    public Portfolio findFirstByUserId(String userId) {
        return portfolioRepository.findFirstByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + userId));
    }

    public Map<LocalDate, Map<String, Object>> getPortfolioTotalHistoryWithDetails(String userId, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Map<String, Map<String, Object>>> history = getPortfolioHistory(userId, startDate, endDate);
        
        Map<LocalDate, Map<String, Object>> result = new TreeMap<>();
        
        for (Map.Entry<LocalDate, Map<String, Map<String, Object>>> entry : history.entrySet()) {
            LocalDate date = entry.getKey();
            Map<String, Map<String, Object>> stocks = entry.getValue();
            
            Map<String, Object> dayData = new HashMap<>();
            double totalValue = 0.0;
            Map<String, Double> stockValues = new HashMap<>();
            
            for (Map.Entry<String, Map<String, Object>> stockEntry : stocks.entrySet()) {
                String symbol = stockEntry.getKey();
                Map<String, Object> details = stockEntry.getValue();
                double stockValue = (double) details.get("totalValue");
                
                stockValues.put(symbol, stockValue);
                totalValue += stockValue;
            }
            
            dayData.put("totalValue", totalValue);
            dayData.put("stockDetails", stockValues);
            result.put(date, dayData);
        }
        
        return result;
    }

    private Portfolio findPortfolioByUserId(String userId) {
        return portfolioRepository.findFirstByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + userId));
    }

    @Scheduled(cron = "0 0 18 * * *")
    private void updateStockPrice(String symbol, LocalDate date) {
        try {
            double price = financeService.getStockPrice(symbol, "USD", date);
            StockHistory stockHistory = new StockHistory();
            stockHistory.setSymbol(symbol);
            stockHistory.setDate(date);
            stockHistory.setPrice(price);
            stockHistoryRepository.save(stockHistory);
            logger.info("Updated price for {} at {}: {}", symbol, date, price);
        } catch (Exception e) {
            logger.error("Error updating price for {}: {}", symbol, e.getMessage());
        }
    }
} 