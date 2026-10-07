package za.co.wethinkcode.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @BeforeEach
    void cleanUsersTable() throws Exception {
        try (Connection connection = Database.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM users");
        }
    }

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

    @Test
    void userCanLoginWithCorrectPassword() throws Exception {
        UserService userService = new UserService();

        userService.register("charlie", "password123");

        assertTrue(
                userService.login("charlie", "password123")
        );
    }

    @Test
    void loginFailsWithIncorrectPassword() throws Exception {
        UserService userService = new UserService();

        userService.register("david", "password123");

        assertFalse(
                userService.login("david", "wrongpassword")
        );
    }

    @Test
    void loginFailsForUnknownUser() throws Exception {
        UserService userService = new UserService();

        UserService service = new UserService();

        assertFalse(
                service.login("unknown", "password123")
        );
    }

    @Test
    void successfulLoginIsLogged() throws Exception {
        UserService userService = new UserService();

        userService.register("eve", "password123");

        assertTrue(
                userService.login("eve", "password123")
        );
    }

    @Test
    void failedLoginIsLogged() throws Exception {
        UserService userService = new UserService();

        userService.register("frank", "password123");

        assertFalse(
                userService.login("frank", "wrongpassword")
        );
    }

    @Test
    void registrationRejectsInvalidUsername() {

        UserService userService = new UserService();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register("", "password123")
        );
    }

    @Test
    void registrationRejectsShortPassword() {

        UserService userService = new UserService();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register("alice", "short")
        );
    }

    @Test
    void successfulLoginCreatesSession() throws Exception {

        UserService userService = new UserService();

        userService.register("george", "password123");

        String token = userService.loginAndCreateSession(
                "george",
                "password123"
        );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void sessionBelongsToLoggedInUser() throws Exception {

        UserService userService = new UserService();

        userService.register("henry", "password123");

        String token = userService.loginAndCreateSession(
                "henry",
                "password123"
        );

        int userId = getUserId("henry");

        assertEquals(
                userId,
                userService.getSessionService().getUserId(token)
        );
    }

    private int getUserId(String username) throws Exception {

        try (Connection connection = Database.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT id FROM users WHERE username = ?"
             )) {

            statement.setString(1, username);

            var result = statement.executeQuery();

            assertTrue(result.next());

            return result.getInt("id");
        }
    }
}