package main.java.com.example.gofido.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanDto {
    private String bank;
    private BigDecimal belopp;
}
