package com.example.demo.service;

import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.repository.PortfolioRepository;
import com.example.demo.repository.StockHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockHistoryRepository stockHistoryRepository;

    @Mock
    private FinanceService financeService;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPortfolio_ShouldReturnNewPortfolio() {
        Portfolio portfolio = new Portfolio();
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        
        Portfolio result = portfolioService.createPortfolio("Test Portfolio");
        assertNotNull(result);
    }

    @Test
    void addStockToPortfolio_ShouldAddStockToPortfolio() {
        String userId = "testUser";
        String symbol = "AAPL";
        int quantity = 5;

        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setUserId(userId);
        portfolio.setStocks(new ArrayList<>());

        when(portfolioRepository.findFirstByUserId(userId)).thenReturn(Optional.of(portfolio));
        when(financeService.getStockPrice(eq(symbol), eq("USD"), any())).thenReturn(150.0);
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        portfolioService.addStockToPortfolio(userId, symbol, quantity);

        verify(portfolioRepository).save(any(Portfolio.class));
        assertEquals(1, portfolio.getStocks().size());
        assertEquals(symbol, portfolio.getStocks().get(0).getSymbol());
        assertEquals(quantity, portfolio.getStocks().get(0).getQuantity());
    }

    @Test
    void findPortfolioByUserId_ShouldReturnPortfolio() {
        String userId = "testUser";
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId(userId);
        portfolio.setStocks(new ArrayList<>());

        when(portfolioRepository.findFirstByUserId(userId)).thenReturn(Optional.of(portfolio));

        Portfolio result = portfolioService.findPortfolioByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(portfolioRepository).findFirstByUserId(userId);
    }
} 