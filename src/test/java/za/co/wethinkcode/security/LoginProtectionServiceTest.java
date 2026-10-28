package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link LoginProtectionService}: counting failed logins and
 * temporarily blocking accounts.
 */
class LoginProtectionServiceTest {

    // Each failed attempt is counted.
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

    // The 5th failed attempt blocks the account.
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

    // 4 failed attempts is still under the limit, so no block yet.
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

    // resetAttempts() (used after a successful login) clears the counter.
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

    // Uses a 1-second lockout to check the account unblocks itself.
    @Test
    void accountIsUnblockedAfterLockoutExpires() throws InterruptedException {
        LoginProtectionService service = new LoginProtectionService(1);

        for (int i = 0; i < 5; i++) {
            service.recordFailedAttempt("alice");
        }

        // Blocked right after the 5th failure.
        assertTrue(
                service.isBlocked("alice")
        );

        // Wait a little longer than the 1-second lockout.
        Thread.sleep(1100);

        // The block is lifted and the counter is reset.
        assertFalse(
                service.isBlocked("alice")
        );

        assertEquals(
                0,
                service.getFailedAttempts("alice")
        );
    }
}
