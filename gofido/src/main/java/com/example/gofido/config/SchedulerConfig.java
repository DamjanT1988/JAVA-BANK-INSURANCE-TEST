package com.example.gofido.config;

import com.example.gofido.repository.OfferRepository;
import com.example.gofido.domain.OfferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private final OfferRepository repo;

    @Scheduled(cron = "0 0 0 * * *")
    public void anonymizeExpiredOffers() {
        LocalDateTime now = LocalDateTime.now();
        List<com.example.gofido.domain.Offer> expired = repo.findAll().stream()
            .filter(o -> o.getStatus() == OfferStatus.SKAPAD && o.getGiltigTill().isBefore(now))
            .toList();
        expired.forEach(o -> { o.setPersonnummer(null); repo.save(o); });
    }
}
