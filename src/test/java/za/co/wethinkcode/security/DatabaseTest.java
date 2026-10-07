package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseTest {

    @Test
    void databaseConnectionWorks() throws Exception {
        Connection connection = Database.getConnection();

        assertNotNull(connection);

        connection.close();
    }

    @Test
    void databaseCanBeInitialized() throws Exception {
        Database.initialize();
    }
}