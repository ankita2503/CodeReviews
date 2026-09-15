package day02.original;

import day02.model.PaymentGateway;
import day02.model.Order;
import day02.model.Payment;
import day02.model.PaymentRepository;

/**
 * Code under review. Intentionally unchanged - do not fix anything here.
 * Write your corrected version in {@code day02.fixed.PaymentProcessorFixed}.
 */
public class PaymentProcessor {

    private final PaymentGateway gateway;
    private final PaymentRepository repo;

    public PaymentProcessor(PaymentGateway gateway, PaymentRepository repo) {
        this.gateway = gateway;
        this.repo = repo;
    }

    public boolean charge(Order order) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                gateway.charge(order.getCustomerId(), order.getAmount());
                repo.markPaid(order.getId());
                return true;
            } catch (Exception e) {
                System.out.println("Retrying...");
            }
        }
        return false;
    }

    public boolean alreadyCharged(Order order) {
        Payment p = repo.findByOrder(order.getId());
        return p.getAmount().equals(order.getAmount());
    }
}
