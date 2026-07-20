package lk.daraz.userservice.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException(String message) {
        super(message);
    }

    public CustomerAlreadyExistsException(String email, boolean isEmail) {
        super("A customer with email '" + email + "' already exists.");
    }
}
