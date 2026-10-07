package za.co.wethinkcode.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class UserService {

    // Register a new user
    public void register(String username, String password) throws Exception {

        String passwordHash = PasswordService.hashPassword(password);

        String sql = """
                INSERT INTO users (username, password_hash, created_at)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, Instant.now().toString());

            statement.executeUpdate();

        } catch (SQLException exception) {
            if (exception.getMessage().contains("UNIQUE")) {
                throw new IllegalArgumentException("Username already exists");
            }

            throw exception;
        }
    }
        // Check user login details
    public boolean login(String username, String password) throws Exception {

        String sql = """
                SELECT password_hash
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            var result = statement.executeQuery();

            if (!result.next()) {
                return false;
            }

            String storedHash = result.getString("password_hash");

            return PasswordService.checkPassword(password, storedHash);
        }
    }
}