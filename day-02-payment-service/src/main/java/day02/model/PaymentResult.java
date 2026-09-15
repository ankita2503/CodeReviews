package day02.model;

/**
 * What the caller needs to know. A boolean cannot distinguish "the card was
 * declined" (tell the customer) from "the gateway is down" (schedule a retry).
 */
public final class PaymentResult {

    public enum Status { PAID, ALREADY_PAID, DECLINED, UNAVAILABLE }

    private final Status status;
    private final String reason;
    private final Throwable cause;

    private PaymentResult(Status status, String reason, Throwable cause) {
        this.status = status;
        this.reason = reason;
        this.cause = cause;
    }

    public static PaymentResult paid() {
        return new PaymentResult(Status.PAID, null, null);
    }

    public static PaymentResult alreadyPaid() {
        return new PaymentResult(Status.ALREADY_PAID, null, null);
    }

    public static PaymentResult declined(String reason) {
        return new PaymentResult(Status.DECLINED, reason, null);
    }

    public static PaymentResult unavailable(Throwable cause) {
        return new PaymentResult(Status.UNAVAILABLE, "payment gateway unavailable", cause);
    }

    public Status getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public Throwable getCause() {
        return cause;
    }

    /** True when the money is taken - whether by this call or an earlier one. */
    public boolean isPaid() {
        return status == Status.PAID || status == Status.ALREADY_PAID;
    }

    /** True when retrying later could still succeed. */
    public boolean isRetryable() {
        return status == Status.UNAVAILABLE;
    }

    @Override
    public String toString() {
        return reason == null ? status.name() : status + " (" + reason + ")";
    }
}
