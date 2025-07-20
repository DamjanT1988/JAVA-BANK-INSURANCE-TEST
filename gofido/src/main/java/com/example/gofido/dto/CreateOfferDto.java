package com.example.gofido.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for creating a new insurance offer.
 * <p>
 * Encapsulates the customer’s personal number, list of loans,
 * and their reported monthly cost. This object is sent by the client
 * in the POST /offer request.
 */
@Data
public class CreateOfferDto {

    /**
     * Customer’s personal identification number (personnummer).
     */
    private String personnummer;

    /**
     * List of loans to be insured, each containing bank name and amount.
     */
    private List<LoanDto> lån;

    /**
     * Monthly loan cost reported by the customer.
     */
    private BigDecimal manadskostnad;
}
