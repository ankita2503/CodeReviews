package day02.fixed;

import day02.model.CardDeclinedException;
import day02.model.IdempotentPaymentGateway;
import day02.model.NetworkException;
import day02.model.Order;
import day02.model.Payment;
import day02.model.PaymentRepository;
import day02.model.PaymentResult;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Day 2 - the corrected version of {@code day02.original.PaymentProcessor}.
 *
 * Every change below carries the review comment that prompted it.
 */
public class PaymentProcessorFixed {

    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BASE_DELAY = Duration.ofMillis(200);

    private final IdempotentPaymentGateway gateway;
    private final PaymentRepository repo;

    public PaymentProcessorFixed(IdempotentPaymentGateway gateway, PaymentRepository repo) {
        // Fails at construction rather than with an NPE on the first charge.
        this.gateway = Objects.requireNonNull(gateway, "gateway");
        this.repo = Objects.requireNonNull(repo, "repo");
    }

    // WAS: boolean.
    // A boolean cannot tell the caller whether the card was declined or the
    // gateway was unreachable, and those need opposite handling - one shows the
    // customer a message, the other schedules a retry.
    public PaymentResult charge(Order order) {

        // Cheap guard against re-entry: a caller that retried after a timeout
        // should not produce a second charge.
        if (alreadyCharged(order)) {
            return PaymentResult.alreadyPaid();
        }

        NetworkException lastFailure = null;
        boolean charged = false;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // WAS: gateway.charge(customerId, amount) with no key.
                // A network timeout is ambiguous - the charge may have succeeded
                // and only the response was lost. Retrying then charges twice.
                // The key lets the gateway recognise a repeat as the same charge.
                // This one cannot be fixed with control flow; the remote side has
                // to cooperate.
                gateway.charge(order.getCustomerId(), order.getAmount(), idempotencyKey(order));
                charged = true;
                break;

            // WAS: a single catch (Exception).
            // That retried a declined card three times - guaranteed to fail, and
            // the bank sees three attempts. It also swallowed genuine bugs: an NPE
            // in our own code was silently retried and reported as "unavailable".
            // Anything not listed here now propagates, which is what we want.
            } catch (CardDeclinedException e) {
                return PaymentResult.declined(e.getMessage());

            } catch (NetworkException e) {
                lastFailure = e;
                // WAS: System.out.println("Retrying...") - no trace, no order id,
                // no attempt number. Nothing to diagnose a production incident with.
                System.out.printf("WARN charge attempt %d/%d failed for order %s: %s%n",
                        attempt, MAX_ATTEMPTS, order.getId(), e);
                backoff(attempt);
            }
        }

        if (!charged) {
            return PaymentResult.unavailable(lastFailure);
        }

        // WAS: repo.markPaid(...) sat inside the try, above the return.
        // THE BUG: gateway succeeds, markPaid throws, the catch sends us back to
        // the top of the loop, and gateway.charge runs again. Three attempts meant
        // three real charges - and the method returned false, so the caller retried
        // the whole thing as well.
        //
        // THE RULE: a retry loop may contain at most one operation with side
        // effects. With two, retrying the block re-runs the first one. Recording
        // the payment is a separate concern and lives outside the loop.
        recordPayment(order);
        return PaymentResult.paid();
    }

    public boolean alreadyCharged(Order order) {
        Payment p = repo.findByOrder(order.getId());

        // WAS: return p.getAmount().equals(order.getAmount());
        //
        // Two bugs on one line. First, findByOrder returns null for an unknown
        // order, so the guard that exists to PREVENT double charging threw instead
        // of returning false. Second, BigDecimal.equals compares scale as well as
        // value, so a stored 49.990 did not equal 49.99 and we reported
        // not-charged for a payment that had happened.
        return p != null && p.getAmount().compareTo(order.getAmount()) == 0;
    }

    /**
     * The card is charged. Recording it must not fail the payment and must never
     * cause another charge, so this swallows and escalates rather than throwing.
     *
     * This is deliberately the ugliest method here, because the situation genuinely
     * is ugly: money has moved and our records disagree. The real fix is a
     * transactional outbox or a reconciliation job. A try/catch only makes it
     * quieter, which is worth saying out loud in the review rather than pretending
     * it is solved.
     */
    private void recordPayment(Order order) {
        try {
            repo.markPaid(order.getId());
        } catch (RuntimeException e) {
            System.out.printf("ERROR PAID BUT UNRECORDED - order %s needs manual reconciliation: %s%n",
                    order.getId(), e);
        }
    }

    private String idempotencyKey(Order order) {
        return "order-" + order.getId();
    }

    /**
     * WAS: nothing - three retries fired as fast as the loop ran, hammering a
     * gateway that was already struggling. Delays double each attempt
     * (200ms, 400ms, 800ms); the random component stops many clients retrying in
     * lockstep and turning a blip into an outage.
     */
    private void backoff(int attempt) {
        long delay = BASE_DELAY.toMillis() * (1L << (attempt - 1))
                + ThreadLocalRandom.current().nextInt(100);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // Restoring the flag is the part everyone forgets. Swallowing an
            // interrupt leaves the thread unable to be shut down, and it is worth
            // watching for in any review that touches sleep or wait.
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during payment retry", e);
        }
    }
}
