package com.pcdiy.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthSchemaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AuthSchemaInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public AuthSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE COMMENT 'username',
                        password_hash VARCHAR(128) NOT NULL COMMENT 'password hash',
                        phone VARCHAR(20) DEFAULT NULL COMMENT 'phone',
                        role VARCHAR(20) DEFAULT 'user' COMMENT 'role',
                        status TINYINT DEFAULT 1 COMMENT 'status',
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        INDEX idx_username (username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='users'
                    """);
        } catch (DataAccessException e) {
            log.warn("Skip auth schema initialization because database is unavailable: {}", e.getMessage());
        }
    }
}
