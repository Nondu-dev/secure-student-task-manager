package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void userCanBeRegistered() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        try (Connection connection = Database.getConnection()) {
            var statement = connection.prepareStatement(
                    "SELECT username, password_hash FROM users WHERE username = ?"
            );

            statement.setString(1, "alice");

            var result = statement.executeQuery();

            assertTrue(result.next());
            assertEquals("alice", result.getString("username"));
            assertTrue(
                    PasswordService.checkPassword(
                            "password123",
                            result.getString("password_hash")
                    )
            );
        }
    }

    @Test
    void duplicateUsernameIsRejected() throws Exception {
        UserService userService = new UserService();

        userService.register("bob", "password123");

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register("bob", "anotherPassword")
        );
    }
}