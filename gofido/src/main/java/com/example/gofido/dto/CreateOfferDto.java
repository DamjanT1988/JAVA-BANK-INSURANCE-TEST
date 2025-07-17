package main.java.com.example.gofido.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOfferDto {
    private String personnummer;
    private List<LoanDto> lån;
    private BigDecimal manadskostnad;
}
