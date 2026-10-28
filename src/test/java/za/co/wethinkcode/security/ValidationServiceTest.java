package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ValidationService}: input validation rules for
 * usernames, passwords and task titles.
 */
class ValidationServiceTest {

    // A normal username is accepted.
    @Test
    void validUsernameIsAccepted() {
        assertTrue(
                ValidationService.isValidUsername("alice")
        );
    }

    // An empty username is rejected.
    @Test
    void emptyUsernameIsRejected() {
        assertFalse(
                ValidationService.isValidUsername("")
        );
    }

    // A username with only spaces counts as blank and is rejected.
    @Test
    void usernameWithOnlySpacesIsRejected() {
        assertFalse(
                ValidationService.isValidUsername("   ")
        );
    }

    // A password of 8 or more characters is accepted.
    @Test
    void validPasswordIsAccepted() {
        assertTrue(
                ValidationService.isValidPassword("password123")
        );
    }

    // An empty password is rejected.
    @Test
    void emptyPasswordIsRejected() {
        assertFalse(
                ValidationService.isValidPassword("")
        );
    }

    // A normal task title is accepted.
    @Test
    void validTaskTitleIsAccepted() {
        assertTrue(
                ValidationService.isValidTaskTitle("Study Java")
        );
    }

    // An empty task title is rejected.
    @Test
    void emptyTaskTitleIsRejected() {
        assertFalse(
                ValidationService.isValidTaskTitle("")
        );
    }

    // A task title with only spaces counts as blank and is rejected.
    @Test
    void taskTitleWithOnlySpacesIsRejected() {
        assertFalse(
                ValidationService.isValidTaskTitle("   ")
        );
    }
}
