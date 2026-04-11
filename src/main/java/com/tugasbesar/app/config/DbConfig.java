package com.tugasbesar.app.config;

public final class DbConfig {

    private DbConfig() {
    }

    public static String getUrl() {
        String host = getValue("DB_HOST", "localhost");
        String port = getValue("DB_PORT", "3306");
        String database = getValue("DB_NAME", "PV_TugasBesar");

        return "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?createDatabaseIfNotExist=true"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Bangkok";
    }

    public static String getUsername() {
        return getValue("DB_USER", "root");
    }

    public static String getPassword() {
        return getValue("DB_PASSWORD", "");
    }

    private static String getValue(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.trim().isEmpty()) {
            return systemValue.trim();
        }

        String envValue = System.getenv(key);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue.trim();
        }

        return defaultValue;
    }
}
