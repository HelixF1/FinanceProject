package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

public class StockRequest {
    private List<String> stockSymbols;  // Hisse senedi kodları listesi
    private String currency;            // İstenen para birimi
    private LocalDate date;             // İstenen tarih

    // Getter ve Setter metodları
    public List<String> getStockSymbols() { return stockSymbols; }
    public void setStockSymbols(List<String> stockSymbols) { this.stockSymbols = stockSymbols; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
} 