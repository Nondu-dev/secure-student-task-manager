package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginProtectionServiceTest {

    @Test
    void failedAttemptsAreTracked() {
        LoginProtectionService service = new LoginProtectionService();

        service.recordFailedAttempt("alice");
        service.recordFailedAttempt("alice");

        assertEquals(
                2,
                service.getFailedAttempts("alice")
        );
    }

    @Test
    void accountIsBlockedAfterFiveFailedAttempts() {
        LoginProtectionService service = new LoginProtectionService();

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt("alice");
        }

        assertTrue(
                service.isBlocked("alice")
        );
    }

    @Test
    void accountIsNotBlockedBeforeFiveFailedAttempts() {
        LoginProtectionService service = new LoginProtectionService();

        for (int i = 0; i < 4; i++) {
            service.recordFailedAttempt("alice");
        }

        assertFalse(
                service.isBlocked("alice")
        );
    }

    @Test
    void successfulLoginResetsFailedAttempts() {
        LoginProtectionService service = new LoginProtectionService();

        service.recordFailedAttempt("alice");
        service.recordFailedAttempt("alice");

        service.resetAttempts("alice");

        assertEquals(
                0,
                service.getFailedAttempts("alice")
        );

        assertFalse(
                service.isBlocked("alice")
        );
    }
}