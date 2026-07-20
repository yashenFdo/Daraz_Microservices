package lk.daraz.wishlistservice.exception;

public class WishlistNotFoundException extends RuntimeException {
    public WishlistNotFoundException(Long customerId) {
        super("Wishlist not found for customer ID: " + customerId);
    }
}
