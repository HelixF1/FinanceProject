package com.example.demo.repository;

import com.example.demo.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findBySymbolOrderByDateAsc(String symbol);
    List<StockHistory> findByDateBetweenAndSymbolInOrderByDateAsc(
        LocalDate startDate, 
        LocalDate endDate, 
        List<String> symbols
    );
    StockHistory findBySymbolAndDate(String symbol, LocalDate date);
} 