package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PasswordService}: BCrypt hashing and checking.
 */
class PasswordServiceTest {

    // The hash exists and is not the plain password.
    @Test
    void passwordCanBeHashed() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
    }

    // The right password matches its hash.
    @Test
    void correctPasswordMatchesHash() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertTrue(PasswordService.checkPassword(password, hash));
    }

    // A wrong password does not match the hash.
    @Test
    void incorrectPasswordDoesNotMatchHash() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertFalse(PasswordService.checkPassword("wrongpassword", hash));
    }

    // Each hash uses a random salt, so the same password gives different hashes.
    @Test
    void samePasswordProducesDifferentHashes() {
        String password = "password123";

        String hash1 = PasswordService.hashPassword(password);
        String hash2 = PasswordService.hashPassword(password);

        assertNotEquals(hash1, hash2);
    }
}
