package com.example.gofido.exception;

/**
 * Thrown when an operation is attempted on an offer that has expired.
 * <p>
 * Ensures that expired offers cannot be accepted or modified.
 */
public class OfferExpiredException extends RuntimeException {

    /**
     * Constructs a new OfferExpiredException for the specified offer ID.
     *
     * @param id the unique identifier of the expired offer
     */
    public OfferExpiredException(String id) {
        // Pass a descriptive message to the base RuntimeException
        super("Offer expired: " + id);
    }
}
