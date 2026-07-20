package lk.daraz.wishlistservice.controller;

import jakarta.validation.Valid;
import lk.daraz.wishlistservice.dto.AddToWishlistRequest;
import lk.daraz.wishlistservice.dto.WishlistResponse;
import lk.daraz.wishlistservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping("/{customerId}")
    public ResponseEntity<WishlistResponse> getWishlist(@PathVariable Long customerId) {
        return ResponseEntity.ok(wishlistService.getWishlist(customerId));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @PathVariable Long customerId,
            @Valid @RequestBody AddToWishlistRequest request) {
        return ResponseEntity.ok(wishlistService.addToWishlist(customerId, request));
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<WishlistResponse> removeWishlistItem(
            @PathVariable Long customerId,
            @PathVariable String productId) {
        return ResponseEntity.ok(wishlistService.removeWishlistItem(customerId, productId));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearWishlist(@PathVariable Long customerId) {
        wishlistService.clearWishlist(customerId);
        return ResponseEntity.noContent().build();
    }
}
