package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    @Test
    void validUsernameIsAccepted() {
        assertTrue(
                ValidationService.isValidUsername("alice")
        );
    }

    @Test
    void emptyUsernameIsRejected() {
        assertFalse(
                ValidationService.isValidUsername("")
        );
    }

    @Test
    void usernameWithOnlySpacesIsRejected() {
        assertFalse(
                ValidationService.isValidUsername("   ")
        );
    }

    @Test
    void validPasswordIsAccepted() {
        assertTrue(
                ValidationService.isValidPassword("password123")
        );
    }

    @Test
    void emptyPasswordIsRejected() {
        assertFalse(
                ValidationService.isValidPassword("")
        );
    }

    @Test
    void validTaskTitleIsAccepted() {
        assertTrue(
                ValidationService.isValidTaskTitle("Study Java")
        );
    }

    @Test
    void emptyTaskTitleIsRejected() {
        assertFalse(
                ValidationService.isValidTaskTitle("")
        );
    }

    @Test
    void taskTitleWithOnlySpacesIsRejected() {
        assertFalse(
                ValidationService.isValidTaskTitle("   ")
        );
    }
}