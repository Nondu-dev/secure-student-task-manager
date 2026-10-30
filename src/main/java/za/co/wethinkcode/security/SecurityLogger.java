package za.co.wethinkcode.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Records security events (LOGIN_SUCCESS, LOGIN_FAILED, ACCOUNT_BLOCKED) in
 * memory and in security.log. Usernames are cleaned first to prevent log injection.
 */
public class SecurityLogger {

    private static final Path LOG_FILE =
            Path.of("security.log");

    // Store security events (synchronized because requests run on several threads)
    private final List<String> events =
            Collections.synchronizedList(new ArrayList<>());

    // Record a security event
    public synchronized void log(String eventType, String username) {

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

    // Write the event to the log file. On Windows another program (for example
    // an antivirus scan or an editor) can lock the file for a moment, so the
    // write is tried a few times before giving up.
    private void writeToFile(String event) {

        IOException lastError = null;

        for (int attempt = 1; attempt <= 5; attempt++) {

            try {
                Files.writeString(
                        LOG_FILE,
                        event + System.lineSeparator(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );

                return;

            } catch (IOException exception) {
                lastError = exception;

                try {
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        throw new RuntimeException(
                "Could not write security log",
                lastError
        );
    }

    // Return recorded security events
    public List<String> getEvents() {
        return Collections.unmodifiableList(events);
    }
}