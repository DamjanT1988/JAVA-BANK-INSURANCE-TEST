package com.example.gofido.dto;

import com.example.gofido.domain.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for returning insurance offer details in API responses.
 * <p>
 * Contains all relevant fields from the Offer entity, including calculated amounts,
 * status, and timestamps.
 */
@Data
@AllArgsConstructor
public class OfferResponseDto {

    /**
     * Unique identifier of the offer.
     */
    private String offerId;

    /**
     * Customer’s personal identification number; may be null if anonymized.
     */
    private String personnummer;

    /**
     * Collection of loans included in the offer.
     */
    private List<LoanDto> lån;

    /**
     * Monthly cost as provided by the customer.
     */
    private BigDecimal manadskostnad;

    /**
     * Total amount insured by the policy (sum of loans).
     */
    private BigDecimal forsakratBelopp;

    /**
     * Calculated premium for the offer.
     */
    private BigDecimal premie;

    /**
     * Current status of the offer (e.g., SKAPAD or TECKNAD).
     */
    private OfferStatus status;

    /**
     * Timestamp when the offer was created.
     */
    private LocalDateTime skapad;

    /**
     * Expiration timestamp for the offer’s validity period.
     */
    private LocalDateTime giltigTill;
}
