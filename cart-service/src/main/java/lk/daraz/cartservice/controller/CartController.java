package lk.daraz.cartservice.controller;

import jakarta.validation.Valid;
import lk.daraz.cartservice.dto.AddToCartRequest;
import lk.daraz.cartservice.dto.CartResponse;
import lk.daraz.cartservice.dto.UpdateCartItemRequest;
import lk.daraz.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Long customerId,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(customerId, request));
    }

    @PatchMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long customerId,
            @PathVariable String productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(customerId, productId, request));
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable Long customerId,
            @PathVariable String productId) {
        return ResponseEntity.ok(cartService.removeCartItem(customerId, productId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
