package za.co.wethinkcode.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static String databaseFile = "task-manager.db";

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile
        );
    }

    public static void useDatabase(String fileName) {
        databaseFile = fileName;
    }

    public static void useApplicationDatabase() {
        databaseFile = "task-manager.db";
    }

    public static void initialize() throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    )
                    """;

            statement.execute(createUsersTable);

            String createTasksTable = """
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT,
                        created_at TEXT NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users(id)
                    )
                    """;

            statement.execute(createTasksTable);
        }
    }
}