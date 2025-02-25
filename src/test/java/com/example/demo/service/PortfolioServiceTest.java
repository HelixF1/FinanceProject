package com.example.demo.service;

import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.model.StockHistory;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.StockHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PortfolioServiceTest {
    
    @Mock
    private PortfolioRepository portfolioRepository;
    
    @Mock
    private StockHistoryRepository stockHistoryRepository;
    
    @Mock
    private FinanceService financeService;
    
    @InjectMocks
    private PortfolioService portfolioService;
    
    private Portfolio testPortfolio;
    private String userId = "testUser";
    
    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setUserId(userId);
        testPortfolio.setStocks(new ArrayList<>());
    }
    
    @Test
    void createPortfolio_Success() {
        when(portfolioRepository.findFirstByUserId(userId))
            .thenReturn(Optional.empty());
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(testPortfolio);
            
        Portfolio result = portfolioService.createPortfolio(userId);
        
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(portfolioRepository).save(any(Portfolio.class));
    }
    
    @Test
    void addStockToPortfolio_Success() {
        String symbol = "AAPL";
        int quantity = 5;
        
        when(portfolioRepository.findFirstByUserId(userId))
            .thenReturn(Optional.of(testPortfolio));
        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(testPortfolio);
        when(financeService.getStockPrice(anyString(), anyString(), any(LocalDate.class)))
            .thenReturn(150.0);
            
        portfolioService.addStockToPortfolio(userId, symbol, quantity);
        
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(stockHistoryRepository).save(any(StockHistory.class));
    }
} 