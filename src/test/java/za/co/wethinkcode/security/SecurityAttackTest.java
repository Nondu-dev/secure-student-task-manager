package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simulates simple attacks to prove the security controls work:
 * reading another user's task, using a fake session token, and
 * SQL injection on login.
 */
class SecurityAttackTest extends TestDatabase {

    // Bob (a different user) must not be able to read Alice's private task.
    @Test
    void userCannotAccessAnotherUsersTask() throws Exception {

        UserService userService = new UserService();

        userService.register("alice", "password123");
        userService.register("bob", "password123");

        TaskService taskService = new TaskService();

        int aliceId = getUserId("alice");
        int bobId = getUserId("bob");

        int aliceTaskId = taskService.createTask(
                aliceId,
                "Private Task",
                "Alice's private information"
        );

        // AuthorizationException is a SecurityException, so this passes.
        assertThrows(
                SecurityException.class,
                () -> taskService.getTask(aliceTaskId, bobId)
        );
    }

    // A made-up session token must be rejected.
    @Test
    void invalidSessionCannotAccessUser() {

        SessionService sessionService = new SessionService();

        assertThrows(
                SecurityException.class,
                () -> sessionService.getUserId(
                        "attacker-made-token"
                )
        );
    }

    // SQL injection text as a username must not bypass login.
    @Test
    void sqlInjectionCannotBypassLogin() throws Exception {

        UserService userService = new UserService();

        userService.register("alice", "password123");

        assertFalse(
                userService.login(
                        "' OR '1'='1",
                        "anything"
                )
        );
    }

    // Helper: finds a user's database ID (the real code keeps this private).
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
