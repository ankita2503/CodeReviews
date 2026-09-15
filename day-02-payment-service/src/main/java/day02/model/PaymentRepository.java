package day02.model;

public interface PaymentRepository {

    /** Returns {@code null} when nothing has been recorded for the order. */
    Payment findByOrder(String orderId);

    void markPaid(String orderId);
}
