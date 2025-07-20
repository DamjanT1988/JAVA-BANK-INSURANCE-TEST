package com.example.gofido.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a single loan entry associated with an insurance offer.
 * <p>
 * This class is embedded into an Offer entity as part of a collection of loans,
 * storing the bank name and the amount of the loan.
 */
@Embeddable  // Indicates that this type can be embedded in another entity
@Data        // Lombok annotation to generate getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

    /**
     * Name of the bank issuing the loan.
     */
    private String bank;

    /**
     * Principal amount of the loan.
     */
    private BigDecimal belopp;
}
