package za.co.wethinkcode.security;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;

/**
 * Base class for tests that need a database.
 *
 * <p>Tests that extend this class run against a separate
 * "test-task-manager.db" file, so the real "task-manager.db" is never
 * changed. The test database is created before the tests, emptied before
 * every test, and deleted afterwards.
 */
public abstract class TestDatabase {

    // The separate database file used only by tests.
    private static final String TEST_DATABASE = "test-task-manager.db";

    /**
     * Runs once before all tests in the class: start with a fresh test
     * database and create the tables.
     */
    @BeforeAll
    static void setUpTestDatabase() throws Exception {
        Files.deleteIfExists(Path.of(TEST_DATABASE));

        Database.useDatabase(TEST_DATABASE);
        Database.initialize();
    }

    /**
     * Runs before every test: delete all rows so each test starts clean
     * and tests cannot affect each other.
     */
    @BeforeEach
    void cleanTestDatabase() throws Exception {

        try (Connection connection = Database.getConnection();
             var statement = connection.createStatement()) {

            // Tasks are deleted first because they refer to users.
            statement.executeUpdate("DELETE FROM tasks");
            statement.executeUpdate("DELETE FROM users");
        }
    }

    /**
     * Runs once after all tests: switch back to the real database and
     * remove the test database file.
     */
    @AfterAll
    static void cleanUpTestDatabase() throws Exception {
        Database.useApplicationDatabase();

        Files.deleteIfExists(Path.of(TEST_DATABASE));
    }
}
