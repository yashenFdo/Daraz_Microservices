package lk.daraz.catalogservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddReviewRequest {

    @NotNull
    private Long customerId;

    @NotBlank
    private String customerName;

    @Min(1) @Max(5)
    private int rating;

    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String comment;
}
