package za.co.wethinkcode.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;

public class UserService {

    private final LoginProtectionService loginProtection =
            new LoginProtectionService();

    private final SecurityLogger securityLogger =
            new SecurityLogger();

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

        // Stop login attempts if the account is blocked
        if (loginProtection.isBlocked(username)) {

            securityLogger.log(
                    "ACCOUNT_BLOCKED",
                    username
            );

            throw new SecurityException(
                    "Too many failed login attempts"
            );
        }

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

                loginProtection.recordFailedAttempt(username);

                securityLogger.log(
                        "LOGIN_FAILED",
                        username
                );

                return false;
            }

            String storedHash = result.getString("password_hash");

            boolean passwordCorrect =
                    PasswordService.checkPassword(password, storedHash);

            if (!passwordCorrect) {

                loginProtection.recordFailedAttempt(username);

                securityLogger.log(
                        "LOGIN_FAILED",
                        username
                );

                return false;
            }

            // Successful login resets failed attempts
            loginProtection.resetAttempts(username);

            securityLogger.log(
                    "LOGIN_SUCCESS",
                    username
            );

            return true;
        }
    }
}