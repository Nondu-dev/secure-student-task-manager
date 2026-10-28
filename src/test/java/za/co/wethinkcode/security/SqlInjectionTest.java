package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that SQL injection does not work against the login.
 * Queries use PreparedStatement, so malicious text is treated as plain data.
 */
class SqlInjectionTest extends TestDatabase {

    // Classic attack: "' OR '1'='1" as the username must not log anyone in.
    @Test
    void maliciousUsernameCannotBypassLogin() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "' OR '1'='1",
                "anything"
        );

        assertFalse(loggedIn);
    }

    // The same attack text used as the password must fail too.
    @Test
    void maliciousPasswordCannotBypassLogin() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "alice",
                "' OR '1'='1"
        );

        assertFalse(loggedIn);
    }

    // Attack text added after a real username is just a name that does not
    // exist, so the login fails even with alice's real password.
    @Test
    void maliciousUsernameIsTreatedAsNormalInput() throws Exception {
        UserService userService = new UserService();

        userService.register("alice", "password123");

        boolean loggedIn = userService.login(
                "alice' OR '1'='1",
                "password123"
        );

        assertFalse(loggedIn);
    }
}
