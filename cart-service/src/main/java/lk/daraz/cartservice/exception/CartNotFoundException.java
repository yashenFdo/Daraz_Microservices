package lk.daraz.cartservice.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(Long customerId) {
        super("Cart not found for customer ID: " + customerId);
    }
}
