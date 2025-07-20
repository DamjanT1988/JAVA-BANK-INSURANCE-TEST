package com.example.gofido.repository;

import com.example.gofido.domain.Offer;
import com.example.gofido.domain.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Repository interface for Offer entities.
 * <p>
 * Extends Spring Data JPA's JpaRepository to provide CRUD operations,
 * as well as custom query methods for statistical reporting and business logic.
 */
public interface OfferRepository extends JpaRepository<Offer, String> {

    /**
     * Count how many offers exist with the given status.
     *
     * @param status the OfferStatus to filter by (e.g. SKAPAD, TECKNAD)
     * @return the number of offers matching the status
     */
    long countByStatus(OfferStatus status);

    /**
     * Count how many offers with the specified status were accepted before a given timestamp.
     * <p>
     * Used to calculate conversion rates by considering only offers accepted within the
     * validity period or before the current time.
     *
     * @param status the status to filter by (should be TECKNAD)
     * @param before LocalDateTime cutoff; only offers with accepteradVid before this are counted
     * @return the count of accepted offers before the specified time
     */
    long countByStatusAndAccepteradVidBefore(OfferStatus status, LocalDateTime before);
}
