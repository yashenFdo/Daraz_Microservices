package lk.daraz.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Fallback controller invoked by the circuit breaker when a downstream
 * service is unavailable. Returns a user-friendly error message.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user-service")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, String>> userServiceFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "User Service is currently unavailable. Please try again later.",
                "service", "user-service"
        ));
    }

    @GetMapping("/catalog-service")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, String>> catalogServiceFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "Catalog Service is currently unavailable. Please try again later.",
                "service", "catalog-service"
        ));
    }

    @GetMapping("/cart-service")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, String>> cartServiceFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "Cart Service is currently unavailable. Please try again later.",
                "service", "cart-service"
        ));
    }

    @GetMapping("/wishlist-service")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<Map<String, String>> wishlistServiceFallback() {
        return Mono.just(Map.of(
                "status", "503",
                "message", "Wishlist Service is currently unavailable. Please try again later.",
                "service", "wishlist-service"
        ));
    }
}
