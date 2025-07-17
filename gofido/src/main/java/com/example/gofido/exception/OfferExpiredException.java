package com.example.gofido.exception;

public class OfferExpiredException extends RuntimeException {
    public OfferExpiredException(String id) { super("Offer expired: " + id); }
}