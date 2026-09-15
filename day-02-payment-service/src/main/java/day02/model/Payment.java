package day02.model;

import java.math.BigDecimal;

/** A payment that has already been taken for an order. */
public class Payment {

    private final String orderId;
    private final BigDecimal amount;

    public Payment(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
