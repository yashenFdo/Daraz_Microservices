package lk.daraz.wishlistservice.service;

import feign.FeignException;
import lk.daraz.wishlistservice.client.CatalogClient;
import lk.daraz.wishlistservice.dto.AddToWishlistRequest;
import lk.daraz.wishlistservice.dto.ProductDto;
import lk.daraz.wishlistservice.dto.WishlistResponse;
import lk.daraz.wishlistservice.entity.Wishlist;
import lk.daraz.wishlistservice.entity.WishlistItem;
import lk.daraz.wishlistservice.exception.ProductNotFoundException;
import lk.daraz.wishlistservice.exception.WishlistNotFoundException;
import lk.daraz.wishlistservice.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final CatalogClient catalogClient;

    @Transactional(readOnly = true)
    public WishlistResponse getWishlist(Long customerId) {
        Wishlist wishlist = wishlistRepository.findById(customerId)
                .orElseGet(() -> createEmptyWishlist(customerId));
        return mapToWishlistResponse(wishlist);
    }

    @Transactional
    public WishlistResponse addToWishlist(Long customerId, AddToWishlistRequest request) {
        ProductDto product;
        try {
            product = catalogClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(request.getProductId());
        }

        if (!product.isActive()) {
            throw new IllegalArgumentException("Product is unavailable.");
        }

        Wishlist wishlist = wishlistRepository.findById(customerId)
                .orElseGet(() -> createEmptyWishlist(customerId));

        boolean alreadyExists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(product.getId()));

        if (!alreadyExists) {
            WishlistItem newItem = WishlistItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .imageUrl(product.getImageUrl())
                    .build();
            wishlist.addItem(newItem);
        }

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("Item added to wishlist for customerId={}", customerId);
        return mapToWishlistResponse(savedWishlist);
    }

    @Transactional
    public WishlistResponse removeWishlistItem(Long customerId, String productId) {
        Wishlist wishlist = wishlistRepository.findById(customerId)
                .orElseThrow(() -> new WishlistNotFoundException(customerId));

        WishlistItem itemToRemove = wishlist.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(productId));

        wishlist.removeItem(itemToRemove);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("Removed item from wishlist for customerId={}", customerId);
        return mapToWishlistResponse(savedWishlist);
    }

    @Transactional
    public void clearWishlist(Long customerId) {
        if (!wishlistRepository.existsById(customerId)) {
            throw new WishlistNotFoundException(customerId);
        }
        wishlistRepository.deleteById(customerId);
        log.info("Cleared wishlist for customerId={}", customerId);
    }

    private Wishlist createEmptyWishlist(Long customerId) {
        return Wishlist.builder()
                .customerId(customerId)
                .build();
    }

    private WishlistResponse mapToWishlistResponse(Wishlist wishlist) {
        List<WishlistResponse.WishlistItemDto> itemDtos = wishlist.getItems().stream()
                .map(item -> WishlistResponse.WishlistItemDto.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .imageUrl(item.getImageUrl())
                        .addedAt(item.getAddedAt())
                        .build())
                .collect(Collectors.toList());

        return WishlistResponse.builder()
                .customerId(wishlist.getCustomerId())
                .items(itemDtos)
                .build();
    }
}
