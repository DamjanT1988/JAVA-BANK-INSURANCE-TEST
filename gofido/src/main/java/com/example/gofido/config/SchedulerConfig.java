package com.example.gofido.config;

import com.example.gofido.repository.OfferRepository;
import com.example.gofido.domain.OfferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Configuration class that schedules periodic tasks for the GOFIDO application.
 * <p>
 * Currently, it includes a job that anonymizes expired offers daily at midnight
 * to comply with GDPR requirements.
 */
@Component
@EnableScheduling  // Enables Spring's scheduled task execution capability
@RequiredArgsConstructor  // Injects final dependencies via constructor
public class SchedulerConfig {

    /**
     * JPA repository for accessing and updating offers.
     */
    private final OfferRepository repo;

    /**
     * Scheduled task that runs every day at midnight (00:00 server time).
     * <p>
     * It finds offers in "SKAPAD" status whose expiry date (giltigTill) is before now,
     * clears their personnummer to anonymize personal data, and saves the changes.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void anonymizeExpiredOffers() {
        // Capture current timestamp
        LocalDateTime now = LocalDateTime.now();

        // Fetch all offers and filter those that are created but expired
        List<com.example.gofido.domain.Offer> expired = repo.findAll().stream()
            .filter(o -> o.getStatus() == OfferStatus.SKAPAD && o.getGiltigTill().isBefore(now))
            .toList();  // Collect into a mutable list if using Java 8+ streams

        // Iterate and clear personal identity numbers, then persist
        expired.forEach(o -> {
            o.setPersonnummer(null);  // Remove sensitive data for GDPR
            repo.save(o);             // Persist the anonymized offer
        });
    }
}
