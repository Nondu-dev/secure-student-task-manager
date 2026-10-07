package za.co.wethinkcode.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityLogger {

    // Store security events
    private final List<String> events = new ArrayList<>();

    // Record a security event
    public void log(String eventType, String username) {

        String event = Instant.now()
                + " | "
                + eventType
                + " | "
                + username;

        events.add(event);
    }

    // Return recorded security events
    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }
}