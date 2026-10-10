package com.harimart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static volatile DatabaseManager instance;
    private HikariDataSource dataSource;

    private DatabaseManager() {
        initDataSource();
        initDatabaseTablesAndSeed();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private void initDataSource() {
        try {
            Properties props = new Properties();
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (is != null) {
                    props.load(is);
                }
            }

            String url = props.getProperty("db.url", "jdbc:h2:file:./data/harimart_db;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1;MODE=LEGACY");
            String username = props.getProperty("db.username", "sa");
            String password = props.getProperty("db.password", "");
            int maxSize = Integer.parseInt(props.getProperty("db.pool.maxSize", "15"));
            int minIdle = Integer.parseInt(props.getProperty("db.pool.minIdle", "5"));

            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.h2.Driver");
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(maxSize);
            config.setMinimumIdle(minIdle);
            config.setPoolName("HariMartHikariPool");
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            this.dataSource = new HikariDataSource(config);
            logger.info("HariMart Database connection pool initialized with URL: {}", url);
        } catch (Exception e) {
            logger.warn("Failed to initialize file-based H2 database, falling back to in-memory: {}", e.getMessage());
            HikariConfig memConfig = new HikariConfig();
            memConfig.setDriverClassName("org.h2.Driver");
            memConfig.setJdbcUrl("jdbc:h2:mem:harimart_db;DB_CLOSE_DELAY=-1;MODE=LEGACY");
            memConfig.setUsername("sa");
            memConfig.setPassword("");
            memConfig.setMaximumPoolSize(10);
            this.dataSource = new HikariDataSource(memConfig);
        }
    }

    private void initDatabaseTablesAndSeed() {
        logger.info("Executing schema.sql and data.sql scripts...");
        executeSqlScript("schema.sql");
        executeSqlScript("data.sql");
        logger.info("Database initialization completed successfully.");
    }

    public void executeSqlScript(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                logger.warn("SQL script not found in classpath: {}", resourcePath);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                StringBuilder sqlBatch = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                        continue;
                    }
                    sqlBatch.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String statementSql = sqlBatch.toString().trim();
                        // remove trailing semicolon
                        if (statementSql.endsWith(";")) {
                            statementSql = statementSql.substring(0, statementSql.length() - 1);
                        }
                        if (!statementSql.isEmpty()) {
                            try {
                                stmt.execute(statementSql);
                            } catch (SQLException ex) {
                                logger.debug("Notice on executing SQL statement (may be duplicate): {}", ex.getMessage());
                            }
                        }
                        sqlBatch.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error executing SQL script {}: {}", resourcePath, e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HariMart Database connection pool closed.");
        }
    }
}
