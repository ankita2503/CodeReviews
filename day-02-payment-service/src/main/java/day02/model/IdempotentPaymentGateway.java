package day02.model;

import java.math.BigDecimal;

/**
 * The same gateway, with the overload the fix needs: a caller-supplied key that
 * lets the provider recognise a retry as the same charge rather than a new one.
 */
public interface IdempotentPaymentGateway extends PaymentGateway {

    void charge(String customerId, BigDecimal amount, String idempotencyKey)
            throws CardDeclinedException, NetworkException;
}
