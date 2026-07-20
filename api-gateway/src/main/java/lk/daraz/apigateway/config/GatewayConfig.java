package lk.daraz.apigateway.config;

import lk.daraz.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ── AUTH routes (public) ────────────────────────────────────
                .route("user-service-auth", r -> r
                        .path("/api/v1/auth/**")
                        .uri("lb://user-service"))

                // ── Customer profile routes (protected) ─────────────────────
                .route("user-service-customers", r -> r
                        .path("/api/v1/customers/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("user-service-cb")
                                        .setFallbackUri("forward:/fallback/user-service")))
                        .uri("lb://user-service"))

                // ── Catalog routes (GET public, mutating protected) ─────────
                .route("catalog-service", r -> r
                        .path("/api/v1/products/**", "/api/v1/categories/**")
                        .filters(f -> f
                                .circuitBreaker(c -> c
                                        .setName("catalog-service-cb")
                                        .setFallbackUri("forward:/fallback/catalog-service")))
                        .uri("lb://catalog-service"))

                // ── Cart routes (protected) ─────────────────────────────────
                .route("cart-service", r -> r
                        .path("/api/v1/carts/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("cart-service-cb")
                                        .setFallbackUri("forward:/fallback/cart-service")))
                        .uri("lb://cart-service"))

                // ── Wishlist routes (protected) ─────────────────────────────
                .route("wishlist-service", r -> r
                        .path("/api/v1/wishlists/**")
                        .filters(f -> f.filter(jwtFilter.apply(new JwtAuthenticationFilter.Config()))
                                .circuitBreaker(c -> c
                                        .setName("wishlist-service-cb")
                                        .setFallbackUri("forward:/fallback/wishlist-service")))
                        .uri("lb://wishlist-service"))

                .build();
    }
}
