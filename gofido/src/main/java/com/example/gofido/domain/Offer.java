package com.example.gofido.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
public class Offer {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String personnummer;

    @ElementCollection
    private List<Loan> loans;

    private BigDecimal manadskostnad;
    private BigDecimal forsakratBelopp;
    private BigDecimal premie;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    private LocalDateTime skapad;
    private LocalDateTime giltigTill;
    private LocalDateTime accepteradVid;
}
