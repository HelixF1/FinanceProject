package com.example.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class DatabaseConfigTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() {
        assertNotNull(dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result);
    }
} 