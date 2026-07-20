package lk.daraz.catalogservice.event;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewAddedEvent {
    private String productId;
    private Long customerId;
    private int rating;
    private LocalDateTime occurredAt;
    private String eventType;

    public static ReviewAddedEvent of(String productId, Long customerId, int rating) {
        return ReviewAddedEvent.builder()
                .productId(productId)
                .customerId(customerId)
                .rating(rating)
                .occurredAt(LocalDateTime.now())
                .eventType("REVIEW_ADDED")
                .build();
    }
}
