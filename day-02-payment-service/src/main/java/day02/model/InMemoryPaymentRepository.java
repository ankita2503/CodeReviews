package day02.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/** Simple stand-in so the processor can be run and reviewed without a database. */
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> paymentsByOrder = new HashMap<>();
    private final Map<String, BigDecimal> amountsByOrder = new HashMap<>();

    /** Registers what an order is worth, so {@link #markPaid} can record it. */
    public void expect(String orderId, BigDecimal amount) {
        amountsByOrder.put(orderId, amount);
    }

    @Override
    public Payment findByOrder(String orderId) {
        return paymentsByOrder.get(orderId);
    }

    @Override
    public void markPaid(String orderId) {
        BigDecimal amount = amountsByOrder.getOrDefault(orderId, BigDecimal.ZERO);
        paymentsByOrder.put(orderId, new Payment(orderId, amount));
    }
}
