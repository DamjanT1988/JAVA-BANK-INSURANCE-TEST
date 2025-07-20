package com.example.gofido.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Global exception handler for all controllers in the GOFIDO application.
 * <p>
 * Catches specific custom exceptions and translates them into appropriate
 * HTTP responses with status codes and error messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle cases where an Offer with the given ID could not be found.
     *
     * @param ex the exception containing the missing-offer ID
     * @return 404 Not Found with a message "Offer not found: {id}"
     */
    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OfferNotFoundException ex) {
        // Respond with HTTP 404 and the exception’s message in the body
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }

    /**
     * Handle cases where an Offer is expired or otherwise invalid to operate on.
     *
     * @param ex the exception containing the expired-offer ID
     * @return 400 Bad Request with a message "Offer expired: {id}"
     */
    @ExceptionHandler(OfferExpiredException.class)
    public ResponseEntity<String> handleExpired(OfferExpiredException ex) {
        // Respond with HTTP 400 because the client tried to act on an expired offer
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ex.getMessage());
    }

    // TODO: If you add an OfferAlreadyAcceptedException, handle it here:
    // @ExceptionHandler(OfferAlreadyAcceptedException.class)
    // public ResponseEntity<String> handleAlreadyAccepted(OfferAlreadyAcceptedException ex) {
    //     return ResponseEntity
    //         .status(HttpStatus.BAD_REQUEST)
    //         .body(ex.getMessage());
    // }
}
