package com.example.demo.controller;

import com.example.demo.service.FinanceService;
import com.example.demo.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceService financeService;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void createPortfolio_Success() throws Exception {
        mockMvc.perform(post("/api/portfolio/create")
                .param("userId", "testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addStock_Success() throws Exception {
        when(financeService.getStockPrice(any(), any(), any())).thenReturn(150.0);

        mockMvc.perform(post("/api/portfolio/add-stock")
                .param("userId", "testUser")
                .param("symbol", "AAPL")
                .param("quantity", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 