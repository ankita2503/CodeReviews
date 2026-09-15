package day02.model;

import java.math.BigDecimal;
import java.util.Objects;

/** The order being paid for. Money is BigDecimal, never double. */
public class Order {

    private final String id;
    private final String customerId;
    private final BigDecimal amount;

    public Order(String id, String customerId, BigDecimal amount) {
        this.id = Objects.requireNonNull(id, "id");
        this.customerId = Objects.requireNonNull(customerId, "customerId");
        this.amount = Objects.requireNonNull(amount, "amount");
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
