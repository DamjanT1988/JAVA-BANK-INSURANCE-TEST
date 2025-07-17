package com.example.gofido.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanDto {
    private String bank;
    private BigDecimal belopp;

    // Tom konstruktor (Lombok kan generera om du vill, men ha gärna kvar den)
    public LoanDto() {}

    // Den här behövs för new LoanDto(bank, belopp)
    public LoanDto(String bank, BigDecimal belopp) {
        this.bank = bank;
        this.belopp = belopp;
    }
}
