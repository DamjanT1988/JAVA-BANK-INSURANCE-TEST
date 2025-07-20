package com.example.gofido.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateOfferDto {
    private String personnummer;
    private List<LoanDto> lån;
    private BigDecimal manadskostnad;
}
