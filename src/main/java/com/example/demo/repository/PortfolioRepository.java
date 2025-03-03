package com.example.demo.repository;

import com.example.demo.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findFirstByUserId(String userId);
    Optional<Portfolio> findByUserId(String userId);
} 