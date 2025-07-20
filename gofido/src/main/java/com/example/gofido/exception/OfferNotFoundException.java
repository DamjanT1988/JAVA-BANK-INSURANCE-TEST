package com.example.gofido.exception;

/**
 * Thrown when an operation is attempted on an offer that does not exist in the repository.
 * <p>
 * Ensures that client requests for non-existent offers are handled gracefully.
 */
public class OfferNotFoundException extends RuntimeException {

    /**
     * Constructs a new OfferNotFoundException for the specified offer ID.
     *
     * @param id the unique identifier of the missing offer
     */
    public OfferNotFoundException(String id) {
        // Pass a descriptive message to the base RuntimeException
        super("Offer not found: " + id);
    }
}
