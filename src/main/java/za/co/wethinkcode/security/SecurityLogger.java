package za.co.wethinkcode.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityLogger {

    private static final Path LOG_FILE =
            Path.of("security.log");

    // Store security events
    private final List<String> events = new ArrayList<>();

    // Record a security event
    public void log(String eventType, String username) {

        String safeUsername = sanitize(username);

        String event = Instant.now()
                + " | "
                + eventType
                + " | "
                + safeUsername;

        events.add(event);

        writeToFile(event);
    }

    // Remove characters that can create fake log lines
    private String sanitize(String value) {

        if (value == null) {
            return "null";
        }

        return value
                .replace("\r", "_")
                .replace("\n", "_");
    }

    // Write the event to the log file
    private void writeToFile(String event) {

        try {
            Files.writeString(
                    LOG_FILE,
                    event + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not write security log",
                    exception
            );
        }
    }

    // Return recorded security events
    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
