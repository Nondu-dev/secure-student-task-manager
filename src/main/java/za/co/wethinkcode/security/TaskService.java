package za.co.wethinkcode.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class TaskService {

    public int createTask(
            int userId,
            String title,
            String description
    ) throws Exception {

        if (!ValidationService.isValidTaskTitle(title)) {
            throw new IllegalArgumentException("Invalid task title");
        }

        String sql = """
                INSERT INTO tasks (user_id, title, description, created_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setInt(1, userId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setString(4, Instant.now().toString());

            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();

            if (keys.next()) {
                return keys.getInt(1);
            }

            throw new SQLException("Could not create task");
        }
    }

    public String getTask(int taskId, int userId) throws Exception {

        String sql = """
                SELECT title, description, user_id
                FROM tasks
                WHERE id = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, taskId);

            var result = statement.executeQuery();

            if (!result.next()) {
                throw new IllegalArgumentException(
                        "Task not found"
                );
            }

            int taskOwnerId =
                    result.getInt("user_id");

            if (taskOwnerId != userId) {
                throw new AuthorizationException(
                        "You are not allowed to access this task"
                );
            }

            return result.getString("title");
        }
    }
}