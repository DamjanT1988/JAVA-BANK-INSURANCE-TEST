package com.example.gofido.controller;

import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.LoanDto;
import com.example.gofido.dto.OfferResponseDto;
import com.example.gofido.dto.UpdateOfferDto;
import com.example.gofido.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST controller that handles HTTP requests for insurance offers.
 * <p>
 * Provides endpoints to create, update, and accept offers, and transforms
 * domain entities into DTOs for external clients.
 */
@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor  // Injects OfferService via constructor
public class OfferController {

    /**
     * Service layer dependency that contains the business logic for offers.
     */
    private final OfferService svc;

    /**
     * Create a new insurance offer.
     *
     * @param dto the incoming data transfer object containing personnummer,
     *            loan details, and monthly cost
     * @return HTTP 200 with the created offer represented as OfferResponseDto
     */
    @PostMapping
    public ResponseEntity<OfferResponseDto> create(@RequestBody CreateOfferDto dto) {
        // Delegate to service and convert the resulting entity to DTO
        var o = svc.createOffer(dto);
        return ResponseEntity.ok(toDto(o));
    }

    /**
     * Update an existing offer by its ID.
     *
     * @param id  the unique identifier of the offer to update
     * @param dto the update DTO containing new loan list and monthly cost
     * @return HTTP 200 with the updated offer as OfferResponseDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<OfferResponseDto> update(
            @PathVariable String id,
            @RequestBody UpdateOfferDto dto) {
        // Call service to apply updates, then map entity to DTO
        var o = svc.updateOffer(id, dto);
        return ResponseEntity.ok(toDto(o));
    }

    /**
     * Accept an offer, marking it as TECKNAD if within validity period.
     *
     * @param id the unique identifier of the offer to accept
     * @return HTTP 200 with the accepted offer as OfferResponseDto
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<OfferResponseDto> accept(@PathVariable String id) {
        // Delegate acceptance logic to service
        var o = svc.acceptOffer(id);
        return ResponseEntity.ok(toDto(o));
    }

    /**
     * Map domain Offer entity to external-facing DTO.
     *
     * @param o the Offer entity from the database
     * @return a fully populated OfferResponseDto
     */
    private OfferResponseDto toDto(com.example.gofido.domain.Offer o) {
        return new OfferResponseDto(
            o.getId(),
            o.getPersonnummer(),
            // Convert each Loan object to LoanDto
            o.getLoans().stream()
                .map(l -> new LoanDto(l.getBank(), l.getBelopp()))
                .collect(Collectors.toList()),
            o.getManadskostnad(),
            o.getForsakratBelopp(),
            o.getPremie(),
            o.getStatus(),
            o.getSkapad(),
            o.getGiltigTill()
        );
    }
}
