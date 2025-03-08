package com.example.demo.controller;

import com.example.demo.model.Portfolio;
import com.example.demo.model.PortfolioStock;
import com.example.demo.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PortfolioService portfolioService;
    
    @InjectMocks
    private PortfolioController portfolioController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void createPortfolio_ShouldReturnNewPortfolio() {
        // Given
        String userId = "testUser";
        Portfolio expectedPortfolio = new Portfolio();
        expectedPortfolio.setUserId(userId);
        when(portfolioService.createPortfolio(userId)).thenReturn(expectedPortfolio);
        
        // When
        Portfolio result = portfolioController.createPortfolio(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }
    
    @Test
    void getHistory_ShouldReturnPortfolioHistory() {
        // Given
        String userId = "testUser";
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        List<Map<String, Object>> expectedHistory = new ArrayList<>();
        Map<String, Object> historyItem = new HashMap<>();
        historyItem.put("Tarih", LocalDate.now().toString());
        historyItem.put("Hisse", "AAPL");
        historyItem.put("Fiyat", 150.0);
        historyItem.put("Adet", 10);
        historyItem.put("Toplam", 1500.0);
        expectedHistory.add(historyItem);
        
        when(portfolioService.getPortfolioHistory(userId, startDate, endDate))
            .thenReturn(expectedHistory);
            
        // When
        List<Map<String, Object>> result = portfolioController.getHistory(userId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(expectedHistory.size(), result.size());
        assertEquals(expectedHistory.get(0).get("Hisse"), result.get(0).get("Hisse"));
        assertEquals(expectedHistory.get(0).get("Toplam"), result.get(0).get("Toplam"));
    }
    
    @Test
    void getUserStocks_ShouldReturnStockList() {
        // Given
        String userId = "testUser";
        Portfolio portfolio = new Portfolio();
        PortfolioStock stock = new PortfolioStock();
        stock.setSymbol("AAPL");
        portfolio.setStocks(Collections.singletonList(stock));
        
        when(portfolioService.findPortfolioByUserId(userId)).thenReturn(portfolio);
        
        // When
        List<String> result = portfolioController.getUserStocks(userId);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("AAPL", result.get(0));
    }
    
    @Test
    void deletePortfolio_ShouldReturnSuccessMessage() {
        // Given
        String userId = "testUser";
        doNothing().when(portfolioService).deletePortfolio(userId);
        
        // When
        ResponseEntity<?> response = portfolioController.deletePortfolio(userId);
        
        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("Portfolio başarıyla silindi", response.getBody());
    }
    
    @Test
    void deletePortfolio_WhenError_ShouldReturnErrorMessage() {
        // Given
        String userId = "nonExistentUser";
        String errorMessage = "Portfolio bulunamadı";
        doThrow(new RuntimeException(errorMessage))
            .when(portfolioService).deletePortfolio(userId);
        
        // When
        ResponseEntity<?> response = portfolioController.deletePortfolio(userId);
        
        // Then
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void createPortfolio_ShouldReturnCreated() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setUserId("testUser");
        when(portfolioService.createPortfolio(anyString())).thenReturn(portfolio);
        
        mockMvc.perform(post("/api/portfolio/create")
                .param("userId", "testUser"))
                .andExpect(status().isCreated());
    }
    
    @Test
    void addStock_Success() throws Exception {
        doNothing().when(portfolioService).addStockToPortfolio(anyString(), anyString(), anyInt());

        mockMvc.perform(post("/api/portfolio/add-stock")
                .param("userId", "testUser")
                .param("symbol", "AAPL")
                .param("quantity", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    void deletePortfolio_Success() throws Exception {
        doNothing().when(portfolioService).deletePortfolio(anyString());

        mockMvc.perform(delete("/api/portfolio/delete")
                .param("userId", "testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 