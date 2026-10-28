package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TaskService}: creating tasks, task ownership and
 * title validation.
 */
class TaskServiceTest extends TestDatabase {

    // A user can create a task and read it back.
    @Test
    void userCanAccessOwnTask() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        TaskService taskService = new TaskService();

        int aliceId = getUserId("alice");

        int taskId = taskService.createTask(
                aliceId,
                "Study Java",
                "Practice Java security"
        );

        assertNotNull(
                taskService.getTask(taskId, aliceId)
        );
    }

    // A user must not be able to read someone else's task.
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
                "Alice Task",
                "Private task"
        );

        assertThrows(
                SecurityException.class,
                () -> taskService.getTask(aliceTaskId, bobId)
        );
    }

    // An empty title is invalid, so task creation must fail.
    @Test
    void taskCreationRejectsEmptyTitle() throws Exception {

        UserService userService = new UserService();

        userService.register("alice", "password123");

        int aliceId = getUserId("alice");

        TaskService taskService = new TaskService();

        assertThrows(
                IllegalArgumentException.class,
                () -> taskService.createTask(
                        aliceId,
                        "",
                        "Private task"
                )
        );
    }

    // Helper: finds a user's database ID.
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
