package lk.daraz.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Simplified Product DTO used by the Feign Client to map the response from Catalog Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String id;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String imageUrl;
    private int stockQuantity;
    private boolean active;
}
