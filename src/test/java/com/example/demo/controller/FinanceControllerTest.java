package com.example.demo.controller;

import com.example.demo.service.FinanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FinanceController.class)
@ActiveProfiles("test")
class FinanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinanceService financeService;

    @Test
    void getStockPrice_ShouldReturnPrice() throws Exception {
        LocalDate testDate = LocalDate.now();
        
        when(financeService.getStockPrice(eq("AAPL"), eq("USD"), any(LocalDate.class)))
                .thenReturn(150.0);

        mockMvc.perform(get("/api/finance/stock-price")
                        .param("symbol", "AAPL")
                        .param("currency", "USD")
                        .param("date", testDate.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getStockPrice_WithInvalidSymbol_ShouldReturnBadRequest() throws Exception {
        LocalDate testDate = LocalDate.now();
        
        when(financeService.getStockPrice(eq(""), eq("USD"), any(LocalDate.class)))
                .thenThrow(new IllegalArgumentException("Invalid symbol"));

        mockMvc.perform(get("/api/finance/stock-price")
                        .param("symbol", "")
                        .param("currency", "USD")
                        .param("date", testDate.toString()))
                .andExpect(status().isBadRequest());
    }
}
