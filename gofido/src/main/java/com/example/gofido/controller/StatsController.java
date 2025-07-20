package com.example.gofido.controller;

import com.example.gofido.domain.OfferStatus;
import com.example.gofido.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST controller providing conversion statistics for insurance offers.
 * <p>
 * Offers endpoints for calculating total offers created, number of offers
 * accepted within their validity period, and the resulting conversion rate.
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    /**
     * Repository for fetching offer data.
     */
    private final OfferRepository repo;

    /**
     * Configurable validity period (in days) for offers, injected from application.properties.
     */
    @Value("${offer.valid-days}")
    private int validDays;

    /**
     * Constructor-based injection of the repository dependency.
     *
     * @param repo the OfferRepository for data access
     */
    public StatsController(OfferRepository repo) {
        this.repo = repo;
    }

    /**
     * GET endpoint to retrieve conversion statistics.
     *
     * @return a map containing:
     *   - "totalaOfferter": total number of offers created
     *   - "accepteradeOfferter": number of offers accepted within validity
     *   - "konverteringsgrad": percentage of accepted offers
     *   - "tidsintervall": validity period description
     */
    @GetMapping("/conversion")
    public Map<String, Object> conversion() {
        // Total offers count
        long total = repo.count();

        // Number of offers with status TECKNAD accepted before now (validity enforced)
        long acceptedWithinValidity =
            repo.countByStatusAndAccepteradVidBefore(
                OfferStatus.TECKNAD,
                LocalDateTime.now()
            );

        // Calculate conversion rate as a percentage
        double rate = total == 0 ? 0 : (acceptedWithinValidity * 100.0) / total;

        // Return statistics in a JSON-friendly map
        return Map.of(
            "totalaOfferter", total,
            "accepteradeOfferter", acceptedWithinValidity,
            "konverteringsgrad", rate,
            "tidsintervall", validDays + " dagar"
        );
    }
}
