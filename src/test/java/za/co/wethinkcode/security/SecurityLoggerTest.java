package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SecurityLoggerTest {

    @Test
    void securityEventCanBeLogged() {

        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_SUCCESS", "alice");

        assertEquals(
                1,
                logger.getEvents().size()
        );
    }

    @Test
    void loggedEventContainsEventType() {

        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");

        assertTrue(
                logger.getEvents().get(0).contains("LOGIN_FAILED")
        );
    }

    @Test
    void loggedEventContainsUsername() {

        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_SUCCESS", "alice");

        assertTrue(
                logger.getEvents().get(0).contains("alice")
        );
    }

    @Test
    void multipleEventsAreStored() {

        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");
        logger.log("LOGIN_FAILED", "alice");
        logger.log("LOGIN_SUCCESS", "alice");

        assertEquals(
                3,
                logger.getEvents().size()
        );
    }

    @Test
    void securityEventIsWrittenToLogFile() throws Exception {

        SecurityLogger logger = new SecurityLogger();

        logger.log("LOGIN_FAILED", "alice");

        Path logFile = Path.of("security.log");

        assertTrue(
                Files.exists(logFile)
        );

        String content = Files.readString(logFile);

        assertTrue(
                content.contains("LOGIN_FAILED")
        );

        assertTrue(
                content.contains("alice")
        );
    }
}