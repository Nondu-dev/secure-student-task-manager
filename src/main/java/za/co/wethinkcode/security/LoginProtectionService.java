package za.co.wethinkcode.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginProtectionService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    // Store failed login attempts for each username
    private final Map<String, Integer> failedAttempts =
            new ConcurrentHashMap<>();

    // Record a failed login attempt
    public void recordFailedAttempt(String username) {
        failedAttempts.merge(username, 1, Integer::sum);
    }

    // Get the number of failed attempts
    public int getFailedAttempts(String username) {
        return failedAttempts.getOrDefault(username, 0);
    }

    // Check if the account is blocked
    public boolean isBlocked(String username) {
        return getFailedAttempts(username) >= MAX_FAILED_ATTEMPTS;
    }

    // Reset attempts after a successful login
    public void resetAttempts(String username) {
        failedAttempts.remove(username);
    }
}