package exceptions;

/**
 * A custom runtime exception intended to be displayed to end users.
 * <p>
 * This exception is used to indicate errors that should be surfaced to users
 * rather than logged or handled internally. It extends {@code RuntimeException}
 * to allow unchecked propagation.
 * </p>
 */
public class UserFacingException extends RuntimeException {
    public UserFacingException(String message) {
        super(message);
    }
}
