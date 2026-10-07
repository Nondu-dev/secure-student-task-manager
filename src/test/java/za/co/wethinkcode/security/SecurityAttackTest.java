package za.co.wethinkcode.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SecurityAttackTest {

    @BeforeEach
    void cleanDatabase() throws Exception {

        try (Connection connection = Database.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM tasks");
            statement.executeUpdate("DELETE FROM users");
        }
    }

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

        assertThrows(
                SecurityException.class,
                () -> taskService.getTask(aliceTaskId, bobId)
        );
    }

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