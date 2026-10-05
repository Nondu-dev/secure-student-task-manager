package za.co.wethinkcode.security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // SQLite database file
    private static final String DATABASE_URL = "jdbc:sqlite:task-manager.db";

    // Connect to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    // Create the database tables
    public static void initialize() throws SQLException {

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Store registered users
            String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL UNIQUE,
                        password_hash TEXT NOT NULL,
                        created_at TEXT NOT NULL
                    )
                    """;

            statement.execute(createUsersTable);

            // Store tasks belonging to users
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