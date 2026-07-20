package lk.daraz.cartservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Redis entity representing a shopping cart.
 * Key will be: cart:{customerId}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("cart")
public class Cart {

    @Id
    private Long customerId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
