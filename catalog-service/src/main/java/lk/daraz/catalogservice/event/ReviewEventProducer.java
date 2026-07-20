package lk.daraz.catalogservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.catalog-events:catalog-events}")
    private String catalogEventsTopic;

    public void publishReviewAdded(ReviewAddedEvent event) {
        kafkaTemplate.send(catalogEventsTopic, event.getProductId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish ReviewAddedEvent for productId={}: {}", event.getProductId(), ex.getMessage());
                    } else {
                        log.info("Published ReviewAddedEvent for productId={}", event.getProductId());
                    }
                });
    }
}
