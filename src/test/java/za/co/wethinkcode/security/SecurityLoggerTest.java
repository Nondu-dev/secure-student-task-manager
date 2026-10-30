package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SecurityLogger}: events are stored, written to
 * "security.log", and protected against log injection.
 */
class SecurityLoggerTest {

    // One logged event is stored in memory.
    @Test
    void securityEventCanBeLogged() {
        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_SUCCESS", "alice");

        assertEquals(1, logger.getEvents().size());
    }

    // The log line includes the event type.
    @Test
    void loggedEventContainsEventType() {
        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");

        assertTrue(
                logger.getEvents()
                        .get(0)
                        .contains("LOGIN_FAILED")
        );
    }

    // The log line includes the username.
    @Test
    void loggedEventContainsUsername() {
        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");

        assertTrue(
                logger.getEvents()
                        .get(0)
                        .contains("alice")
        );
    }

    // Several events are all kept.
    @Test
    void multipleEventsAreStored() {
        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");
        logger.log("LOGIN_SUCCESS", "bob");

        assertEquals(2, logger.getEvents().size());
    }

    // The event is also saved in the security.log file.
    @Test
    void securityEventIsWrittenToLogFile() throws Exception {
        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_SUCCESS", "alice");

        Path logFile = Path.of("security.log");

        assertTrue(Files.exists(logFile));

        String content = Files.readString(logFile);

        assertTrue(content.contains("LOGIN_SUCCESS"));
        assertTrue(content.contains("alice"));
    }

    // Log injection: a username containing a line break must not create a
    // fake second log line.
    @Test
    void usernameNewlineIsSanitizedInLog() throws Exception {
        Path logFile = Path.of("security.log");

        // Start from a clean log file so old entries cannot affect the result.
        Files.deleteIfExists(logFile);

        SecurityLogger logger = new SecurityLogger();

        // The attacker tries to fake a successful login on a new line.
        String maliciousUsername =
                "alice\nLOGIN_SUCCESS | hacker";

        logger.log(
                "LOGIN_FAILED",
                maliciousUsername
        );

        String content = Files.readString(logFile);

        // The raw line break must be gone from the file.
        assertFalse(
                content.contains(
                        "alice\nLOGIN_SUCCESS | hacker"
                )
        );

        // Only one line was written, and the line break became an underscore.
        assertEquals(1, content.lines().count());
        assertTrue(content.contains("alice_LOGIN_SUCCESS | hacker"));
    }
}
