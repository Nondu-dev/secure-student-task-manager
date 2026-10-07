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

    private final SessionService sessionService =
            new SessionService();

    // Register a new user
    public void register(String username, String password) throws Exception {

        // Validate user input
        if (!ValidationService.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username");
        }

        if (!ValidationService.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

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

    // Login and create a session
    public String loginAndCreateSession(
            String username,
            String password
    ) throws Exception {

        boolean loginSuccessful = login(username, password);

        if (!loginSuccessful) {
            throw new SecurityException(
                    "Invalid username or password"
            );
        }

        int userId = getUserId(username);

        return sessionService.createSession(userId);
    }

    // Get the database ID of a user
    private int getUserId(String username) throws Exception {

        String sql = """
                SELECT id
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            var result = statement.executeQuery();

            if (!result.next()) {
                throw new IllegalArgumentException(
                        "User not found"
                );
            }

            return result.getInt("id");
        }
    }

    // Get the session service
    public SessionService getSessionService() {
        return sessionService;
    }
}