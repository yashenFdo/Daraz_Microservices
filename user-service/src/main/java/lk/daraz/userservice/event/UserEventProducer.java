package lk.daraz.userservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka producer that publishes user-related domain events.
 * Uses KafkaTemplate with async send + callback logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.user-events:user-events}")
    private String userEventsTopic;

    /**
     * Publishes a UserRegisteredEvent to the user-events topic.
     * The key is the customer ID for ordered consumption per customer.
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        String key = String.valueOf(event.getCustomerId());
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(userEventsTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish UserRegisteredEvent for customerId={}: {}",
                        event.getCustomerId(), ex.getMessage());
            } else {
                log.info("Published UserRegisteredEvent for customerId={} to topic={} partition={} offset={}",
                        event.getCustomerId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
