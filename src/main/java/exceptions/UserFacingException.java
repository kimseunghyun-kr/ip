package exceptions;

public class UserFacingException extends RuntimeException {
  public UserFacingException(String message) {
    super(message);
  }
}
