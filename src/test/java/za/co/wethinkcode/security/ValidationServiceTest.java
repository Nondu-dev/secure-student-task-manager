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

    // A password shorter than 8 characters is rejected; 8 is accepted.
    @Test
    void shortPasswordIsRejected() {
        assertFalse(
                ValidationService.isValidPassword("1234567")
        );

        assertTrue(
                ValidationService.isValidPassword("12345678")
        );
    }

    // A missing value (null) is rejected instead of causing an error.
    @Test
    void nullValuesAreRejected() {
        assertFalse(ValidationService.isValidUsername(null));
        assertFalse(ValidationService.isValidPassword(null));
        assertFalse(ValidationService.isValidTaskTitle(null));
    }

    // A username can be at most 50 characters.
    @Test
    void tooLongUsernameIsRejected() {
        assertTrue(
                ValidationService.isValidUsername("a".repeat(50))
        );

        assertFalse(
                ValidationService.isValidUsername("a".repeat(51))
        );
    }

    // A task title can be at most 100 characters.
    @Test
    void tooLongTaskTitleIsRejected() {
        assertTrue(
                ValidationService.isValidTaskTitle("a".repeat(100))
        );

        assertFalse(
                ValidationService.isValidTaskTitle("a".repeat(101))
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
