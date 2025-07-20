package com.example.gofido.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object representing a loan in an offer.
 * <p>
 * Contains the bank name and amount for a single loan entry.
 * Used within CreateOfferDto and UpdateOfferDto requests and
 * returned in OfferResponseDto.
 */
@Data
public class LoanDto {

    /**
     * Name of the bank issuing the loan.
     */
    private String bank;

    /**
     * Principal amount of the loan.
     */
    private BigDecimal belopp;

    /**
     * No-args constructor required for JSON deserialization.
     */
    public LoanDto() {
        // Default constructor
    }

    /**
     * All-args constructor for convenient instantiation in code.
     *
     * @param bank   the bank name
     * @param belopp the loan amount
     */
    public LoanDto(String bank, BigDecimal belopp) {
        this.bank = bank;
        this.belopp = belopp;
    }
}
