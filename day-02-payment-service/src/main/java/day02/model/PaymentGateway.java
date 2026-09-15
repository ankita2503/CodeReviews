package day02.model;

import java.math.BigDecimal;

/** The remote payment provider, as the code under review sees it. */
public interface PaymentGateway {

    void charge(String customerId, BigDecimal amount)
            throws CardDeclinedException, NetworkException;
}
