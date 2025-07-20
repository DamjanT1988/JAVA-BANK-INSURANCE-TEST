package com.example.gofido.exception;

/**
 * Thrown when an operation is attempted on an offer that has already been accepted.
 * <p>
 * Ensures that accepted offers cannot be modified or re-accepted.
 */
public class OfferAlreadyAcceptedException extends RuntimeException {

    /**
     * Constructs a new OfferAlreadyAcceptedException for the specified offer ID.
     *
     * @param id the unique identifier of the already accepted offer
     */
    public OfferAlreadyAcceptedException(String id) {
        // Pass a descriptive message to the base RuntimeException
        super("Offer already accepted: " + id);
    }
}
