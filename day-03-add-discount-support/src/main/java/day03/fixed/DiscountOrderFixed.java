package day03.fixed;

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
public class DiscountOrderFixed implements Comparable<DiscountOrderFixed> {

    private static final BigDecimal TAX_RATE = TaxRates.STANDARD;

    private final String id;
    private final String customerId;
    private final BigDecimal amount; // amount is still mutable Make it final and it becomes impossible by construction.
    private BigDecimal discount = BigDecimal.ZERO; //To prevent NPE when discount is null add = BigDecimal.ZERO

    public DiscountOrderFixed(String id, String customerId, BigDecimal amount) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getAmount() { return amount; }

    public BigDecimal getDiscount() { return discount; }

    public void setDiscount(BigDecimal discount) {
        Objects.requireNonNull(discount, "discount");
        if (discount.compareTo(BigDecimal.ZERO) < 0 ||
                discount.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount must be between 0 and 1");
        }
        this.discount = discount;
    }

    public BigDecimal total() {
        BigDecimal discountedAmount = amount.multiply(
                BigDecimal.ONE.subtract((discount))
        );

        return discountedAmount.add(discountedAmount.multiply(TAX_RATE));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountOrderFixed)) return false;
        DiscountOrderFixed other = (DiscountOrderFixed) o;
        return id.equals(other.id) && amount.compareTo(other.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount.stripTrailingZeros());//stripTrailingZeros for equals
    }


    //A TreeSet uses compareTo() to determine whether something is already in the set.
    @Override
    public int compareTo(DiscountOrderFixed other) {
        int amountComparison = this.amount.compareTo(other.amount);

        if (amountComparison != 0) {
            return amountComparison;
        }

        return this.id.compareTo(other.id);
    }
}
