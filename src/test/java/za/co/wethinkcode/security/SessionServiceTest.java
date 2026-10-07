package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    @Test
    void sessionCanBeCreated() {
        SessionService sessionService = new SessionService();

        String token = sessionService.createSession(1);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void sessionReturnsCorrectUser() {
        SessionService sessionService = new SessionService();

        String token = sessionService.createSession(1);

        assertEquals(
                1,
                sessionService.getUserId(token)
        );
    }

    @Test
    void differentSessionsHaveDifferentTokens() {
        SessionService sessionService = new SessionService();

        String token1 = sessionService.createSession(1);
        String token2 = sessionService.createSession(1);

        assertNotEquals(token1, token2);
    }

    @Test
    void invalidTokenIsRejected() {
        SessionService sessionService = new SessionService();

        assertThrows(
                SecurityException.class,
                () -> sessionService.getUserId("invalid-token")
        );
    }

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
}