package com.tugasbesar.app.database;

import com.tugasbesar.app.config.DbConfig;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DriverPropertyInfo;
import java.util.Properties;
import java.util.logging.Logger;

public final class DatabaseConnection {
    private static volatile boolean driverLoaded;

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
        if (driverLoaded) {
            return;
        }
        synchronized (DatabaseConnection.class) {
            if (driverLoaded) {
                return;
            }
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
                return;
            } catch (ClassNotFoundException ignored) {
                // fallback to local lib loading below
            }

            File driverJar = new File("lib/mysql-connector-j-8.0.33.jar");
            if (!driverJar.exists()) {
                throw new SQLException(
                        "Komponen database belum siap. Jalankan aplikasi melalui konfigurasi project yang benar atau pastikan library database tersedia.",
                        new ClassNotFoundException("lib/mysql-connector-j-8.0.33.jar tidak ditemukan")
                );
            }

            try {
                URL jarUrl = driverJar.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, DatabaseConnection.class.getClassLoader());
                Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver", true, loader).newInstance();
                DriverManager.registerDriver(new DriverShim(driver));
                driverLoaded = true;
            } catch (Exception exception) {
                throw new SQLException(
                        "Komponen database belum siap. Jalankan aplikasi melalui konfigurasi project yang benar atau pastikan library database tersedia.",
                        exception
                );
            }
        }
    }

    private static final class DriverShim implements Driver {
        private final Driver driver;

        private DriverShim(Driver driver) {
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public Logger getParentLogger() {
            try {
                return driver.getParentLogger();
            } catch (Exception exception) {
                return Logger.getGlobal();
            }
        }
    }
}
