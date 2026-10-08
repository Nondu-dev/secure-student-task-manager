package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseTest extends TestDatabase {

    @Test
    void databaseConnectionWorks() throws Exception {

        try (Connection connection = Database.getConnection()) {
            assertNotNull(connection);
        }
    }

    @Test
    void databaseCanBeInitialized() throws Exception {
        Database.initialize();
    }
}