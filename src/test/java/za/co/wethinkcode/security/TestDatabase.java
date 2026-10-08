package za.co.wethinkcode.security;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

public abstract class TestDatabase {

    private static final String TEST_DATABASE = "test-task-manager.db";

    @BeforeAll
    static void setUpTestDatabase() throws Exception {
        Files.deleteIfExists(Path.of(TEST_DATABASE));

        Database.useDatabase(TEST_DATABASE);
        Database.initialize();
    }

    @BeforeEach
    void cleanTestDatabase() throws Exception {

        try (Connection connection = Database.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM tasks");
            statement.executeUpdate("DELETE FROM users");
        }
    }

    @AfterAll
    static void cleanUpTestDatabase() throws Exception {
        Database.useApplicationDatabase();

        Files.deleteIfExists(Path.of(TEST_DATABASE));
    }
}