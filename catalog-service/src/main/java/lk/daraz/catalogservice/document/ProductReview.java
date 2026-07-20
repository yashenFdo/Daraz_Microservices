package lk.daraz.catalogservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Embedded review document stored within a Product.
 * Embedding is preferred here as reviews are always accessed with their product.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReview {

    private String reviewId;
    private Long customerId;
    private String customerName;
    private int rating;           // 1–5
    private String title;
    private String comment;
    private boolean verified;
    private LocalDateTime createdAt;
}
