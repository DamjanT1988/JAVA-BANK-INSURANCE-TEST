package com.example.gofido.controller;

import com.example.gofido.domain.OfferStatus;
import com.example.gofido.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final OfferRepository repo;

    @Value("${offer.valid-days}")
    private int validDays;

    public StatsController(OfferRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/conversion")
    public Map<String, Object> conversion() {
        long total = repo.count();
        // Räkna alla accepterade TECKNAD före nuvarande tid
        long acceptedWithinValidity =
            repo.countByStatusAndAccepteradVidBefore(OfferStatus.TECKNAD, LocalDateTime.now());
        double rate = total == 0 ? 0 : acceptedWithinValidity * 100.0 / total;

        return Map.of(
            "totalaOfferter", total,
            "accepteradeOfferter", acceptedWithinValidity,
            "konverteringsgrad", rate,
            "tidsintervall", validDays + " dagar"
        );
    }
}
