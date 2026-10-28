package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link Database}: connecting and creating the tables.
 */
class DatabaseTest extends TestDatabase {

    // A connection can be opened and is not null.
    @Test
    void databaseConnectionWorks() throws Exception {

        try (Connection connection = Database.getConnection()) {
            assertNotNull(connection);
        }
    }

    // Calling initialize() again must not fail (tables already exist).
    @Test
    void databaseCanBeInitialized() throws Exception {
        Database.initialize();
    }
}
