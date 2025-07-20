package com.example.gofido.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for updating an existing insurance offer.
 * <p>
 * Contains fields the client is allowed to modify: the personal number,
 * the list of loans, and the monthly cost. Used in the PUT /offer/{id} request.
 */
@Data
@NoArgsConstructor
public class UpdateOfferDto {

    /**
     * Updated personal identification number for the customer.
     * <p>
     * Will replace the existing personnummer on the offer.
     */
    private String personnummer;

    /**
     * New collection of loans to insure.
     * <p>
     * The service will recalculate total insured amount and premium based on this list.
     */
    private List<LoanDto> lån;

    /**
     * Updated monthly cost provided by the customer.
     */
    private BigDecimal manadskostnad;
}
