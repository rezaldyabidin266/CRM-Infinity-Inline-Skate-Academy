package com.tugasbesar.app.database;

import com.tugasbesar.app.config.DbConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DbConfig.getUrl(),
                DbConfig.getUsername(),
                DbConfig.getPassword()
        );
    }
}
