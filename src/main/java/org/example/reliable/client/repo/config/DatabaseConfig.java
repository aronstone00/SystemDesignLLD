package org.example.reliable.client.repo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class DatabaseConfig {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/reliable_tasks";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "password";
    
    private static HikariDataSource dataSource;
    
    static {
        initializeDataSource();
        createTablesIfNotExist();
    }
    
    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("ReliableTasksPool");
        
        dataSource = new HikariDataSource(config);
        log.info("Database connection pool initialized");
    }
    
    private static void createTablesIfNotExist() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS jobs (
                job_id VARCHAR(255) PRIMARY KEY,
                status VARCHAR(50) NOT NULL,
                payload TEXT,
                payload_hash VARCHAR(255),
                reference_id VARCHAR(255),
                handler_name VARCHAR(255),
                response TEXT,
                error TEXT,
                created_at BIGINT NOT NULL,
                updated_at BIGINT NOT NULL
            )
            """;
        
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            log.info("Jobs table created/verified successfully");
        } catch (SQLException e) {
            log.error("Error creating jobs table", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    public static DataSource getDataSource() {
        return dataSource;
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            log.info("Database connection pool closed");
        }
    }
}
