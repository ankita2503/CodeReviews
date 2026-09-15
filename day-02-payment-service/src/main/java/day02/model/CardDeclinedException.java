package day02.model;

/**
 * The bank said no. Terminal - retrying the same card cannot change the answer,
 * and every attempt is visible to the issuer.
 */
public class CardDeclinedException extends Exception {

    public CardDeclinedException(String message) {
        super(message);
    }
}
