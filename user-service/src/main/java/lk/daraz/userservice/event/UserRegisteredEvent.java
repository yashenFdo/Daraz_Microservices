package lk.daraz.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Kafka event published when a new customer registers.
 * Consumed by: Notification Service (send welcome email).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisteredEvent {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registeredAt;
    private String eventType;

    public static UserRegisteredEvent of(Long id, String firstName, String lastName, String email) {
        return UserRegisteredEvent.builder()
                .customerId(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .registeredAt(LocalDateTime.now())
                .eventType("USER_REGISTERED")
                .build();
    }
}
