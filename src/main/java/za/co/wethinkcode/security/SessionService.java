package za.co.wethinkcode.security;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    private final long sessionDurationSeconds;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionService() {
        this(1800);
    }

    SessionService(long sessionDurationSeconds) {
        this.sessionDurationSeconds = sessionDurationSeconds;
    }

    public String createSession(int userId) {
        String token = UUID.randomUUID().toString();

        Instant expiryTime = Instant.now().plusSeconds(sessionDurationSeconds);

        sessions.put(token, new Session(userId, expiryTime));

        return token;
    }

    public int getUserId(String token) {

        Session session = sessions.get(token);

        if (session == null) {
            throw new SecurityException("Invalid session");
        }

        if (Instant.now().isAfter(session.expiryTime())) {
            sessions.remove(token);
            throw new SecurityException("Session expired");
        }

        return session.userId();
    }

    public void removeSession(String token) {
        sessions.remove(token);
    }

    private record Session(int userId, Instant expiryTime) {
    }
}