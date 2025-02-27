package com.example.demo.repository;

import com.example.demo.model.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    List<StockHistory> findBySymbolInAndDateBetweenOrderByDateAsc(
        List<String> symbols, 
        LocalDate startDate, 
        LocalDate endDate
    );
} 