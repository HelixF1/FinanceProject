package com.example.demo.controller;

import com.example.demo.model.Portfolio;
import com.example.demo.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PortfolioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void createPortfolio_Success() throws Exception {
        Portfolio testPortfolio = new Portfolio();
        testPortfolio.setUserId("testUser");
        
        when(portfolioService.createPortfolio(anyString())).thenReturn(testPortfolio);

        mockMvc.perform(post("/api/portfolio/create")
                .param("userId", "testUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("testUser"));
    }

    @Test
    void addStock_Success() throws Exception {
        mockMvc.perform(post("/api/portfolio/add-stock")
                .param("userId", "testUser")
                .param("symbol", "AAPL")
                .param("quantity", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
} 