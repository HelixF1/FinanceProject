package com.example.demo.dto;

import java.time.LocalDate;

public class StockHistoryDTO {
    private LocalDate date;
    private String symbol;
    private double price;
    private int quantity;
    private double totalValue;

    public StockHistoryDTO(LocalDate date, String symbol, double price, int quantity, double totalValue) {
        this.date = date;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.totalValue = totalValue;
    }

    public LocalDate getDate() { return date; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public double getTotalValue() { return totalValue; }
} 