package com.example.gofido.exception;

public class OfferAlreadyAcceptedException extends RuntimeException {
    public OfferAlreadyAcceptedException(String id) {
        super("Offer already accepted: " + id);
    }
}
