package lk.daraz.cartservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents an individual item within a Cart.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    
    private String productId;
    private String productName;
    private String sku;
    private BigDecimal price;
    private int quantity;
    private String imageUrl;
}
