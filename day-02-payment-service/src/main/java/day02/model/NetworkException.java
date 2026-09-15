package day02.model;

/**
 * The gateway could not be reached, or the response was lost. Retryable - but
 * ambiguous: the charge may already have gone through, which is why the retry
 * has to carry an idempotency key.
 */
public class NetworkException extends Exception {

    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
