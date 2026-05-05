package com.tugasbesar.app.database;

import com.tugasbesar.app.config.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();
        return DriverManager.getConnection(
                DbConfig.getUrl(),
                DbConfig.getUsername(),
                DbConfig.getPassword()
        );
    }

    private static void ensureDriverLoaded() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "MySQL JDBC driver tidak ditemukan di runtime. Jalankan aplikasi lewat run.bat atau Run MainApp, dan pastikan lib/mysql-connector-j-8.0.33.jar ikut di classpath.",
                    exception
            );
        }
    }
}
