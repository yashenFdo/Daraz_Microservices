package lk.daraz.catalogservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal discountedPrice;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    private String brand;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stockQuantity;

    private String imageUrl;
    private List<String> imageUrls;
    private Map<String, Object> attributes;
}
