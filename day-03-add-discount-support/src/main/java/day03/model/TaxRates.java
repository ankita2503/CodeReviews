package day03.model;

import java.math.BigDecimal;

/** Tax rates the order total is computed against. */
public final class TaxRates {

    /** Standard sales tax applied on top of the order amount. */
    public static final BigDecimal STANDARD = new BigDecimal("0.08");

    private TaxRates() {
    }
}
