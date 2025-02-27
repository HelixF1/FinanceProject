package com.example.demo.service;

import com.example.demo.dto.StockHistoryDTO;
import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.model.StockHistory;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.StockHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    
    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);
    
    @Autowired
    private PortfolioRepository portfolioRepository;
    
    @Autowired
    private StockHistoryRepository stockHistoryRepository;
    
    @Autowired
    private FinanceService financeService;

    // Portfolio oluşturma
    public Portfolio createPortfolio(String userId) {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setStocks(new ArrayList<>());
        return portfolioRepository.save(portfolio);
    }

    // Portfolyoya hisse ekleme
    public void addStockToPortfolio(String userId, String symbol, int quantity) {
        Portfolio portfolio = findPortfolioByUserId(userId);
        
        // Eğer hisse zaten varsa miktarını güncelle
        Optional<PortfolioStock> existingStock = portfolio.getStocks().stream()
            .filter(s -> s.getSymbol().equals(symbol))
            .findFirst();
            
        if (existingStock.isPresent()) {
            existingStock.get().setQuantity(existingStock.get().getQuantity() + quantity);
        } else {
            // Yeni hisse ekle
            PortfolioStock newStock = new PortfolioStock();
            newStock.setSymbol(symbol);
            newStock.setQuantity(quantity);
            newStock.setPortfolio(portfolio);
            portfolio.getStocks().add(newStock);
        }
        
        portfolioRepository.save(portfolio);
        
        // Hisse eklendiğinde ilk fiyat kaydını al
        updateSingleStockPrice(symbol);
    }

    // Belirli bir hissenin fiyatını güncelle
    private void updateSingleStockPrice(String symbol) {
        try {
            LocalDate today = LocalDate.now();
            double price = financeService.getStockPrice(symbol, "USD", today);
            
            StockHistory stockHistory = new StockHistory();
            stockHistory.setSymbol(symbol);
            stockHistory.setDate(today);
            stockHistory.setPrice(price);
            stockHistoryRepository.save(stockHistory);
            
            logger.info("Updated price for {} at {}: {}", symbol, today, price);
        } catch (Exception e) {
            logger.error("Error updating price for {}: {}", symbol, e.getMessage());
        }
    }

    // Her gün saat 18:00'de çalışacak otomatik güncelleme
    @Scheduled(cron = "0 0 18 * * *")
    public void scheduledPriceUpdate() {
        logger.info("Starting scheduled price update for all stocks");
        
        try {
            // Tüm portfolyolardaki benzersiz hisseleri bul ve güncelle
            portfolioRepository.findAll().stream()
                .flatMap(portfolio -> portfolio.getStocks().stream())
                .map(PortfolioStock::getSymbol)
                .distinct()
                .forEach(this::updateSingleStockPrice);
                
            logger.info("Completed scheduled price update successfully");
        } catch (Exception e) {
            logger.error("Error during scheduled price update: {}", e.getMessage());
        }
    }

    // Portfolyo bulma yardımcı metodu
    public Portfolio findPortfolioByUserId(String userId) {
        return portfolioRepository.findFirstByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Portfolio not found for user: " + userId));
    }

    // Portfolyo geçmişini getir
    public List<Map<String, Object>> getPortfolioHistory(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            Portfolio portfolio = findPortfolioByUserId(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            // Her hisse için tek bir satır oluştur
            for (PortfolioStock stock : portfolio.getStocks()) {
                Map<String, Object> stockData = new HashMap<>();
                double price = financeService.getStockPrice(stock.getSymbol(), "USD", LocalDate.now());
                double total = price * stock.getQuantity();
                
                // Her hisse için tek bir satır için veri hazırla
                stockData.put("Tarih", LocalDate.now().toString());
                stockData.put("Hisse", stock.getSymbol());
                // Fiyat ve toplam değerleri 2 ondalık basamağa yuvarla
                stockData.put("Fiyat", Math.round(price * 100.0) / 100.0);
                stockData.put("Adet", stock.getQuantity());
                stockData.put("Toplam", Math.round(total * 100.0) / 100.0);
                
                result.add(stockData);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("Error getting portfolio history: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Portfolyonun toplam değerini hesapla
    public Map<LocalDate, Double> getPortfolioTotalValue(String userId, LocalDate startDate, LocalDate endDate) {
        Portfolio portfolio = findPortfolioByUserId(userId);
        List<String> symbols = portfolio.getStocks().stream()
            .map(PortfolioStock::getSymbol)
            .collect(Collectors.toList());
            
        return stockHistoryRepository.findBySymbolInAndDateBetweenOrderByDateAsc(
                symbols, startDate, endDate)
            .stream()
            .collect(Collectors.groupingBy(
                StockHistory::getDate,
                TreeMap::new,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .mapToDouble(sh -> {
                            PortfolioStock ps = portfolio.getStocks().stream()
                                .filter(s -> s.getSymbol().equals(sh.getSymbol()))
                                .findFirst()
                                .orElseThrow();
                            return sh.getPrice() * ps.getQuantity();
                        })
                        .sum()
                )
            ));
    }
} 