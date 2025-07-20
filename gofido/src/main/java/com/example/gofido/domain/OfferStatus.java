package com.example.gofido.domain;

/**
 * Enumeration of possible states for an insurance offer in the GOFIDO system.
 * <p>
 * Defines whether an offer has been created and is still valid (SKAPAD),
 * or has been accepted by the customer (TECKNAD).
 */
public enum OfferStatus {

    /** Offer has been created but not yet accepted. */
    SKAPAD,

    /** Offer has been accepted by the customer. */
    TECKNAD
}
