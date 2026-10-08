package za.co.wethinkcode.security;

public class AuthorizationException extends SecurityException {

    public AuthorizationException(String message) {
        super(message);
    }
}