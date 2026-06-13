package com.tugasbesar.app.database;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DatabaseMigrator {
    private static final String MIGRATIONS_DIR = "database\\migrations";

    private DatabaseMigrator() {
    }

    public static void migrate() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            ensureMigrationTable(connection);

            for (MigrationFile migrationFile : loadMigrationFiles()) {
                if (isApplied(connection, migrationFile.version)) {
                    continue;
                }

                applyMigration(connection, migrationFile);
            }
        } catch (IOException | SQLException exception) {
            showDatabaseError(exception);
        }
    }

    private static void ensureMigrationTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS schema_migrations ("
                    + "version VARCHAR(50) PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                    + ")");
        }
    }

    private static List<MigrationFile> loadMigrationFiles() throws IOException {
        Path migrationsPath = Paths.get(MIGRATIONS_DIR);
        if (!Files.exists(migrationsPath)) {
            return new ArrayList<>();
        }

        try (Stream<Path> files = Files.list(migrationsPath)) {
            return files
                    .filter(path -> path.getFileName().toString().matches("^V\\d+__.+\\.sql$"))
                    .sorted(Comparator.comparingInt(DatabaseMigrator::migrationVersionNumber))
                    .map(DatabaseMigrator::toMigrationFile)
                    .collect(Collectors.toList());
        }
    }

    private static int migrationVersionNumber(Path path) {
        String fileName = path.getFileName().toString();
        int separatorIndex = fileName.indexOf("__");
        String versionText = fileName.substring(1, separatorIndex);
        return Integer.parseInt(versionText);
    }

    private static MigrationFile toMigrationFile(Path path) {
        String fileName = path.getFileName().toString();
        int separatorIndex = fileName.indexOf("__");
        String version = fileName.substring(0, separatorIndex);
        String name = fileName.substring(separatorIndex + 2, fileName.length() - 4);
        return new MigrationFile(version, name, path);
    }

    private static boolean isApplied(Connection connection, String version) throws SQLException {
        String sql = "SELECT version FROM schema_migrations WHERE version = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, version);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static void applyMigration(Connection connection, MigrationFile migrationFile) throws SQLException, IOException {
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);

        try {
            for (String sql : parseSqlStatements(migrationFile.path)) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO schema_migrations (version, name, applied_at) VALUES (?, ?, ?)")) {
                statement.setString(1, migrationFile.version);
                statement.setString(2, migrationFile.name);
                statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException | IOException exception) {
            connection.rollback();
            throw exception;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    private static List<String> parseSqlStatements(Path path) throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                continue;
            }

            current.append(line).append('\n');
            if (trimmed.endsWith(";")) {
                String sql = current.toString().trim();
                statements.add(sql.substring(0, sql.length() - 1).trim());
                current.setLength(0);
            }
        }

        if (current.length() > 0) {
            statements.add(current.toString().trim());
        }

        return statements;
    }

    private static void showDatabaseError(Exception exception) {
        JOptionPane.showMessageDialog(
                null,
                "Gagal menjalankan migrasi database.\n\nPeriksa konfigurasi database dan file migrasi.\n\nDetail: " + exception.getMessage(),
                "Database Migration Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static final class MigrationFile {
        private final String version;
        private final String name;
        private final Path path;

        private MigrationFile(String version, String name, Path path) {
            this.version = version;
            this.name = name;
            this.path = path;
        }
    }
}
