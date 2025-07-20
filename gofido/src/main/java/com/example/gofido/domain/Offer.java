package com.example.gofido.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA entity representing an insurance offer in the GOFIDO system.
 * <p>
 * Contains all relevant data fields including personal identifier,
 * associated loans, calculated amounts, status, and timestamps.
 */
@Entity
@Table(name = "offers")
@Data            // Lombok annotation to generate getters, setters, equals, hashCode, toString
@NoArgsConstructor
public class Offer {

    /**
     * Unique identifier for the offer, generated as a UUID string.
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    /**
     * Personal identification number of the customer; cleared when anonymized.
     */
    private String personnummer;

    /**
     * List of loans embedded in the offer, each with bank name and amount.
     */
    @ElementCollection
    private List<Loan> loans;

    /**
     * Monthly cost input by the customer, used for display and reporting.
     */
    private BigDecimal manadskostnad;

    /**
     * Total amount insured, calculated as the sum of all loan amounts.
     */
    private BigDecimal forsakratBelopp;

    /**
     * Calculated premium, typically 3.8% of the insured amount.
     */
    private BigDecimal premie;

    /**
     * Current status of the offer (e.g., SKAPAD, TECKNAD).
     */
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    /**
     * Timestamp when the offer was created; used to enforce validity.
     */
    private LocalDateTime skapad;

    /**
     * Expiration timestamp; after this, the offer cannot be accepted.
     */
    private LocalDateTime giltigTill;

    /**
     * Timestamp when the offer was accepted; null if not yet accepted.
     */
    private LocalDateTime accepteradVid;
}
