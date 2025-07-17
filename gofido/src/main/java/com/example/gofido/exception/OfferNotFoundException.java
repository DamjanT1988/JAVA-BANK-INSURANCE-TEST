package com.example.gofido.exception;

public class OfferNotFoundException extends RuntimeException {
    public OfferNotFoundException(String id) { super("Offer not found: " + id); }
}