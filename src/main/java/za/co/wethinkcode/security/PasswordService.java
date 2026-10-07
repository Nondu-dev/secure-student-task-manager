package za.co.wethinkcode.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

    // Hash a password before storing it
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Check a password against its hash
    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}