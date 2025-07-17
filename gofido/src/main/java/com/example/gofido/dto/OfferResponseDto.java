package com.example.gofido.dto;

import com.example.gofido.domain.OfferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OfferResponseDto {
    private String offerId;
    private String personnummer;
    private List<LoanDto> lån;
    private BigDecimal manadskostnad;
    private BigDecimal forsakratBelopp;
    private BigDecimal premie;
    private OfferStatus status;
    private LocalDateTime skapad;
    private LocalDateTime giltigTill;
}
