package lk.daraz.catalogservice.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private String categoryId;
    private String categoryName;
    private String brand;
    private int stockQuantity;
    private String imageUrl;
    private List<String> imageUrls;
    private Map<String, Object> attributes;
    private double averageRating;
    private int reviewCount;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
