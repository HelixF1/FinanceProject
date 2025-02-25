package com.example.demo.repository;

import com.example.demo.model.PortfolioStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PortfolioStockRepository extends JpaRepository<PortfolioStock, Long> {
    List<PortfolioStock> findByPortfolioId(Long portfolioId);
} 