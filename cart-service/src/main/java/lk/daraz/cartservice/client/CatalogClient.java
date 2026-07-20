package lk.daraz.cartservice.client;

import lk.daraz.cartservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client to communicate with Catalog Service.
 * Allows retrieving product details required when adding items to cart.
 */
@FeignClient(name = "catalog-service", path = "/api/v1/products")
public interface CatalogClient {

    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable("id") String id);
}
