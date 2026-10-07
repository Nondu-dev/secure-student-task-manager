package za.co.wethinkcode.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginProtectionService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private final long lockoutDurationSeconds;

    private final Map<String, Integer> failedAttempts =
            new ConcurrentHashMap<>();

    private final Map<String, Instant> lockoutTimes =
            new ConcurrentHashMap<>();

    public LoginProtectionService() {
        this(300);
    }

    LoginProtectionService(long lockoutDurationSeconds) {
        this.lockoutDurationSeconds = lockoutDurationSeconds;
    }

    public void recordFailedAttempt(String username) {
        int attempts = failedAttempts.merge(username, 1, Integer::sum);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockoutTimes.put(username, Instant.now());
        }
    }

    public int getFailedAttempts(String username) {
        return failedAttempts.getOrDefault(username, 0);
    }

    public boolean isBlocked(String username) {

        Instant lockoutTime = lockoutTimes.get(username);

        if (lockoutTime == null) {
            return false;
        }

        if (Instant.now().isAfter(
                lockoutTime.plusSeconds(lockoutDurationSeconds))) {

            lockoutTimes.remove(username);
            failedAttempts.remove(username);

            return false;
        }

        return true;
    }

    public void resetAttempts(String username) {
        failedAttempts.remove(username);
        lockoutTimes.remove(username);
    }
}