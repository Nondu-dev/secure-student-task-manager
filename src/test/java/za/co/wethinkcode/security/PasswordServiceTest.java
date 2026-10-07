package za.co.wethinkcode.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {

    @Test
    void passwordCanBeHashed() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
    }

    @Test
    void correctPasswordMatchesHash() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertTrue(PasswordService.checkPassword(password, hash));
    }

    @Test
    void incorrectPasswordDoesNotMatchHash() {
        String password = "password123";

        String hash = PasswordService.hashPassword(password);

        assertFalse(PasswordService.checkPassword("wrongpassword", hash));
    }

    @Test
    void samePasswordProducesDifferentHashes() {
        String password = "password123";

        String hash1 = PasswordService.hashPassword(password);
        String hash2 = PasswordService.hashPassword(password);

        assertNotEquals(hash1, hash2);
    }
}