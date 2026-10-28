package za.co.wethinkcode.security;

/**
 * Thrown when a logged-in user tries to access something they do not own
 * (for example, another user's task). The API turns it into HTTP 403.
 */
public class AuthorizationException extends SecurityException {

    public AuthorizationException(String message) {
        super(message);
    }
}