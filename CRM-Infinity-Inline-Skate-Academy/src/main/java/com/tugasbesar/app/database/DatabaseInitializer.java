package com.tugasbesar.app.database;

@Deprecated
public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    @Deprecated
    public static void initialize() {
        DatabaseMigrator.migrate();
    }
}
