package day03.original;

import day03.model.TaxRates;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Code under review. Intentionally unchanged - do not fix anything here.
 * Write your corrected version in {@code day03.fixed.Order}.
 *
 * <p>The change under review is the "add discount support" patch: the new
 * {@code discount} field and its getter/setter.
 */
public class DiscountOrderOriginal implements Comparable<DiscountOrderOriginal> {

    private static final BigDecimal TAX_RATE = TaxRates.STANDARD;

    private final String id;
    private final String customerId;
    private BigDecimal amount;
    private double discount;

    public DiscountOrderOriginal(String id, String customerId, BigDecimal amount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }

    public double getDiscount() { return discount; }

    public void setDiscount(double discount) {
        this.discount = discount;
        this.amount = amount.multiply(BigDecimal.valueOf(1 - discount));
    }

    public BigDecimal total() {
        return amount.add(amount.multiply(TAX_RATE));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountOrderOriginal)) return false;
        DiscountOrderOriginal other = (DiscountOrderOriginal) o;
        return id.equals(other.id) && amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }

    @Override
    public int compareTo(DiscountOrderOriginal other) {
        return this.amount.compareTo(other.amount);
    }
}
