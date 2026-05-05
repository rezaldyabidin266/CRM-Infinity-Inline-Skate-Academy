import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class RepairAttendanceMigration {
    public static void main(String[] args) throws Exception {
        String host = getValue("DB_HOST", "localhost");
        String port = getValue("DB_PORT", "3306");
        String database = getValue("DB_NAME", "PV_TugasBesar");
        String username = getValue("DB_USER", "root");
        String password = getValue("DB_PASSWORD", "root");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?createDatabaseIfNotExist=true"
                + "&useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=Asia/Bangkok"
                + "&connectTimeout=5000"
                + "&socketTimeout=15000";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                execute(statement, "DROP TABLE IF EXISTS attendance_form_levels");
                execute(statement, "DROP TABLE IF EXISTS attendance_forms");
                execute(statement, "DROP TABLE IF EXISTS attendance_records");
                execute(statement, "DELETE FROM schema_migrations WHERE version IN ('V15', 'V16', 'V17')");
                connection.commit();
                System.out.println("Repair migration attendance berhasil.");
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    private static void execute(Statement statement, String sql) throws SQLException {
        System.out.println("Executing: " + sql);
        statement.execute(sql);
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
