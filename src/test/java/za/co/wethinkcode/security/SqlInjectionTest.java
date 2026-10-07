package za.co.wethinkcode.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SqlInjectionTest {

    @BeforeEach
    void cleanUsersTable() throws Exception {
        try (Connection connection = Database.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM users");
        }
    }

    @Test
    void maliciousUsernameCannotBypassLogin() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "' OR '1'='1",
                "anything"
        );

        assertFalse(loggedIn);
    }

    @Test
    void maliciousPasswordCannotBypassLogin() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "alice",
                "' OR '1'='1"
        );

        assertFalse(loggedIn);
    }

    @Test
    void maliciousUsernameIsTreatedAsNormalInput() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "alice' OR '1'='1",
                "password123"
        );

        assertFalse(loggedIn);
    }
}