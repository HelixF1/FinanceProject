package com.example.demo.model;

import java.time.LocalDate;
import java.util.List;

public class StockRequest {
    private List<String> symbols;
    private String currency;
    private LocalDate date;

    // Getters and Setters
    public List<String> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
} 