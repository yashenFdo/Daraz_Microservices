package lk.daraz.catalogservice.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String parentCategoryId;
    private boolean active;
    private LocalDateTime createdAt;
}
