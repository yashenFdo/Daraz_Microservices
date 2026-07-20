package lk.daraz.cartservice.service;

import feign.FeignException;
import lk.daraz.cartservice.client.CatalogClient;
import lk.daraz.cartservice.dto.AddToCartRequest;
import lk.daraz.cartservice.dto.CartResponse;
import lk.daraz.cartservice.dto.ProductDto;
import lk.daraz.cartservice.dto.UpdateCartItemRequest;
import lk.daraz.cartservice.exception.CartNotFoundException;
import lk.daraz.cartservice.exception.ProductNotFoundException;
import lk.daraz.cartservice.model.Cart;
import lk.daraz.cartservice.model.CartItem;
import lk.daraz.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CatalogClient catalogClient;

    public CartResponse getCart(Long customerId) {
        Cart cart = cartRepository.findById(customerId)
                .orElseGet(() -> createEmptyCart(customerId));
        return mapToCartResponse(cart);
    }

    public CartResponse addToCart(Long customerId, AddToCartRequest request) {
        ProductDto product;
        try {
            product = catalogClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(request.getProductId());
        }

        if (!product.isActive() || product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Product is unavailable or out of stock.");
        }

        Cart cart = cartRepository.findById(customerId).orElseGet(() -> createEmptyCart(customerId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .sku(product.getSku())
                    .price(product.getDiscountedPrice() != null ? product.getDiscountedPrice() : product.getPrice())
                    .quantity(request.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("Item added to cart for customerId={}", customerId);
        return mapToCartResponse(savedCart);
    }

    public CartResponse updateCartItem(Long customerId, String productId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findById(customerId)
                .orElseThrow(() -> new CartNotFoundException(customerId));

        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(productId));

        item.setQuantity(request.getQuantity());
        Cart savedCart = cartRepository.save(cart);
        
        log.info("Updated item quantity in cart for customerId={}", customerId);
        return mapToCartResponse(savedCart);
    }

    public CartResponse removeCartItem(Long customerId, String productId) {
        Cart cart = cartRepository.findById(customerId)
                .orElseThrow(() -> new CartNotFoundException(customerId));

        boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        if (!removed) {
            throw new ProductNotFoundException(productId);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("Removed item from cart for customerId={}", customerId);
        return mapToCartResponse(savedCart);
    }

    public void clearCart(Long customerId) {
        if (!cartRepository.existsById(customerId)) {
            throw new CartNotFoundException(customerId);
        }
        cartRepository.deleteById(customerId);
        log.info("Cleared cart for customerId={}", customerId);
    }

    private Cart createEmptyCart(Long customerId) {
        return Cart.builder()
                .customerId(customerId)
                .items(new ArrayList<>())
                .build();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartResponse.CartItemDto> itemDtos = cart.getItems().stream()
                .map(item -> CartResponse.CartItemDto.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .sku(item.getSku())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getImageUrl())
                        .subTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return CartResponse.builder()
                .customerId(cart.getCustomerId())
                .items(itemDtos)
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}
