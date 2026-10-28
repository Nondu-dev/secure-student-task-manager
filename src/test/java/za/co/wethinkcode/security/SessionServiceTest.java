package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SessionService}: creating, checking, expiring and
 * removing sessions.
 */
class SessionServiceTest {

    // A new session returns a non-empty token.
    @Test
    void sessionCanBeCreated() {
        SessionService sessionService = new SessionService();

        String token = sessionService.createSession(1);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    // The token maps back to the user it was created for.
    @Test
    void sessionReturnsCorrectUser() {
        SessionService sessionService = new SessionService();

        String token = sessionService.createSession(1);

        assertEquals(
                1,
                sessionService.getUserId(token)
        );
    }

    // Every session gets its own unique token.
    @Test
    void differentSessionsHaveDifferentTokens() {
        SessionService sessionService = new SessionService();

        String token1 = sessionService.createSession(1);
        String token2 = sessionService.createSession(1);

        assertNotEquals(token1, token2);
    }

    // A token that was never issued is rejected.
    @Test
    void invalidTokenIsRejected() {
        SessionService sessionService = new SessionService();

        assertThrows(
                SecurityException.class,
                () -> sessionService.getUserId("invalid-token")
        );
    }

    // After logout (removeSession) the token no longer works.
    @Test
    void sessionCanBeLoggedOut() {
        SessionService sessionService = new SessionService();

        String token = sessionService.createSession(1);

        sessionService.removeSession(token);

        assertThrows(
                SecurityException.class,
                () -> sessionService.getUserId(token)
        );
    }

    // Uses a 1-second session, waits for it to expire, then checks it is rejected.
    @Test
    void expiredSessionIsRejected() throws InterruptedException {
        SessionService sessionService = new SessionService(1);

        String token = sessionService.createSession(1);

        // Wait a little longer than the 1-second session length.
        Thread.sleep(1100);

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> sessionService.getUserId(token)
        );

        assertEquals("Session expired", exception.getMessage());
    }
}
