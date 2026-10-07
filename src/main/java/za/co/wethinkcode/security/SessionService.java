package za.co.wethinkcode.security;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    // Store session tokens and their users
    private final Map<String, Integer> sessions = new ConcurrentHashMap<>();

    // Create a secure random session token
    public String createSession(int userId) {
        String token = UUID.randomUUID().toString();

        sessions.put(token, userId);

        return token;
    }

    // Get the user linked to a session
    public int getUserId(String token) {

        Integer userId = sessions.get(token);

        if (userId == null) {
            throw new SecurityException("Invalid session");
        }

        return userId;
    }

    // Remove a session during logout
    public void removeSession(String token) {
        sessions.remove(token);
    }
}