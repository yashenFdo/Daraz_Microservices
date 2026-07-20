package lk.daraz.catalogservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MongoDB Document representing a Daraz product.
 * The schema-less nature of MongoDB handles the variable attributes
 * across different product categories (electronics vs. clothing etc.)
 */
@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    private String id;

    @TextIndexed(weight = 3)
    private String name;

    @TextIndexed(weight = 2)
    private String description;

    @Indexed(unique = true)
    private String sku;

    private BigDecimal price;
    private BigDecimal discountedPrice;

    @Indexed
    private String categoryId;
    private String categoryName;

    private String brand;

    @Builder.Default
    private int stockQuantity = 0;

    private String imageUrl;

    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    /** Flexible key-value attributes per product category */
    private Map<String, Object> attributes;

    @Builder.Default
    private double averageRating = 0.0;

    @Builder.Default
    private int reviewCount = 0;

    @Builder.Default
    private List<ProductReview> reviews = new ArrayList<>();

    @Builder.Default
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
