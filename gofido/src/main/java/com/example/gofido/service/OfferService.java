package com.example.gofido.service;

import com.example.gofido.domain.Loan;
import com.example.gofido.domain.Offer;
import com.example.gofido.domain.OfferStatus;
import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.LoanDto;
import com.example.gofido.dto.UpdateOfferDto;
import com.example.gofido.exception.OfferAlreadyAcceptedException;
import com.example.gofido.exception.OfferExpiredException;
import com.example.gofido.exception.OfferNotFoundException;
import com.example.gofido.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing insurance offers.
 * <p>
 * Contains core business logic for creating, updating, and accepting offers,
 * including premium calculations, validity checks, and exception handling.
 */
@Service
@RequiredArgsConstructor  // Constructor injection of repository
public class OfferService {

    /**
     * JPA repository for CRUD operations on offers.
     */
    private final OfferRepository repo;

    /**
     * Validity period in days for newly created offers, injected from configuration.
     */
    @Value("${offer.valid-days}")
    private int validDays;

    /**
     * Create a new insurance offer based on client-provided data.
     * <p>
     * Steps:
     * 1. Map DTO loans to domain Loan entities.
     * 2. Calculate total insured amount and premium (3.8%).
     * 3. Initialize status, creation timestamp, and expiry timestamp.
     * 4. Persist the offer.
     *
     * @param dto the create-offer data transfer object
     * @return the persisted Offer entity with generated ID and timestamps
     */
    public Offer createOffer(CreateOfferDto dto) {
        Offer o = new Offer();
        o.setPersonnummer(dto.getPersonnummer());
        // Map each LoanDto to a Loan entity
        o.setLoans(dto.getLån().stream()
                .map(l -> new Loan(l.getBank(), l.getBelopp()))
                .collect(Collectors.toList()));
        o.setManadskostnad(dto.getManadskostnad());

        // Sum up all loan amounts to determine insured amount
        BigDecimal total = o.getLoans().stream()
                .map(Loan::getBelopp)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        o.setForsakratBelopp(total);
        // Premium is 3.8% of total insured amount
        o.setPremie(total.multiply(BigDecimal.valueOf(0.038)));

        // Set initial status and timestamps
        o.setStatus(OfferStatus.SKAPAD);
        o.setSkapad(LocalDateTime.now());
        o.setGiltigTill(o.getSkapad().plusDays(validDays));

        // Persist and return the new offer
        return repo.save(o);
    }

    /**
     * Accept an existing offer, marking it as TECKNAD if still valid.
     * <p>
     * Throws exceptions if the offer does not exist or is expired.
     *
     * @param id the unique identifier of the offer to accept
     * @return the updated Offer entity
     * @throws OfferNotFoundException    if no offer found for the given ID
     * @throws OfferExpiredException     if the offer has already expired
     */
    public Offer acceptOffer(String id) {
        // Retrieve offer or throw if not found
        Offer o = repo.findById(id)
                .orElseThrow(() -> new OfferNotFoundException(id));
        // Prevent accepting expired offers
        if (LocalDateTime.now().isAfter(o.getGiltigTill())) {
            throw new OfferExpiredException(id);
        }
        // Update status and timestamp for acceptance
        o.setStatus(OfferStatus.TECKNAD);
        o.setAccepteradVid(LocalDateTime.now());
        return repo.save(o);
    }

    /**
     * Update an existing offer’s details.
     * <p>
     * Validates status and expiry before applying changes.
     * Recalculates insured amount and premium based on updated loans.
     *
     * @param id  the unique identifier of the offer to update
     * @param dto the update-offer DTO with new loan list and monthly cost
     * @return the updated Offer entity
     * @throws OfferNotFoundException         if the offer does not exist
     * @throws OfferAlreadyAcceptedException  if the offer has already been accepted
     * @throws OfferExpiredException          if the offer has expired
     */
    public Offer updateOffer(String id, UpdateOfferDto dto) {
        // Load existing offer or throw if missing
        Offer o = repo.findById(id)
                .orElseThrow(() -> new OfferNotFoundException(id));
        // Prevent updates on already accepted offers
        if (o.getStatus() != OfferStatus.SKAPAD) {
            throw new OfferAlreadyAcceptedException(id);
        }
        // Prevent updates on expired offers
        if (LocalDateTime.now().isAfter(o.getGiltigTill())) {
            throw new OfferExpiredException(id);
        }

        // Apply new personal number and monthly cost
        o.setPersonnummer(dto.getPersonnummer());
        o.setManadskostnad(dto.getManadskostnad());

        // Map and set new list of loans
        List<Loan> loans = dto.getLån().stream()
                .map(l -> new Loan(l.getBank(), l.getBelopp()))
                .collect(Collectors.toList());
        o.setLoans(loans);

        // Recompute total insured amount and premium
        BigDecimal total = loans.stream()
                .map(Loan::getBelopp)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        o.setForsakratBelopp(total);
        o.setPremie(total.multiply(BigDecimal.valueOf(0.038)));

        // Persist the updated offer
        return repo.save(o);
    }
}
