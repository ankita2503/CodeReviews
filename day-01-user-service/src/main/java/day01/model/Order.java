package day01.model;

import java.math.BigDecimal;

public class Order {

    private final String id;
    private final double amount;
    private final String status;

    public Order(String id, double amount, String status) {
        this.id = id;
        this.amount = amount;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}
