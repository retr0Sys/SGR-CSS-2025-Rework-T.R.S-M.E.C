package com.css.restaurante.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestiona la conexión a la base de datos mediante un pool de conexiones
 * HikariCP.
 * Las credenciales se cargan desde un archivo .env en la raíz del proyecto.
 */
public class ConexionDB {

    private static HikariDataSource dataSource;

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dotenv.get("DB_URL", "jdbc:postgresql://localhost:5432/restaurantecss"));
            config.setUsername(dotenv.get("DB_USER", "postgres"));
            config.setPassword(dotenv.get("DB_PASS", ""));

            // Configuración del pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(10000);
            config.setMaxLifetime(1800000);

            // Optimizaciones PostgreSQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Seguridad y monitoreo
            config.setPoolName("SGR-Pool");
            config.setLeakDetectionThreshold(60000); // Detecta conexiones no cerradas > 60s

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("[SGR] Error al inicializar el pool de conexiones.");
        }
    }

    /**
     * Obtiene una conexión del pool.
     * IMPORTANTE: siempre cerrar la conexión en un try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("El pool de conexiones no está inicializado.");
        }
        return dataSource.getConnection();
    }

    /**
     * Cierra el pool de conexiones al finalizar la aplicación.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
