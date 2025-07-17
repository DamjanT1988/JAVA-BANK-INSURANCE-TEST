package com.example.gofido.repository;

import com.example.gofido.domain.Offer;
import com.example.gofido.domain.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface OfferRepository extends JpaRepository<Offer, String> {
    long countByStatus(OfferStatus status);
    long countByStatusAndAccepteradVidBefore(OfferStatus status, LocalDateTime before);
}
