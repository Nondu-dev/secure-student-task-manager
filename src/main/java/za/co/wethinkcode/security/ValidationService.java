package za.co.wethinkcode.security;

public class ValidationService {

    // Check username
    public static boolean isValidUsername(String username) {
        return username != null
                && !username.isBlank()
                && username.length() <= 50;
    }

    // Check password
    public static boolean isValidPassword(String password) {
        return password != null
                && !password.isBlank()
                && password.length() >= 8;
    }

    // Check task title
    public static boolean isValidTaskTitle(String title) {
        return title != null
                && !title.isBlank()
                && title.length() <= 100;
    }
}