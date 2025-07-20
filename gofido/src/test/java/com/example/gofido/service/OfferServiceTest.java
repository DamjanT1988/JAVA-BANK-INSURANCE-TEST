/**
 * Unit tests for {@link com.example.gofido.service.OfferService}.
 * <p>
 * Verifies core business logic including offer creation, premium calculation,
 * acceptance rules, and update behavior using Mockito for repository mocking.
 */
package com.example.gofido.service;

import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.LoanDto;
import com.example.gofido.dto.UpdateOfferDto;
import com.example.gofido.domain.Offer;
import com.example.gofido.domain.OfferStatus;
import com.example.gofido.exception.OfferAlreadyAcceptedException;
import com.example.gofido.exception.OfferExpiredException;
import com.example.gofido.exception.OfferNotFoundException;
import com.example.gofido.repository.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    /**
     * Mocked repository to simulate database operations.
     */
    @Mock
    private OfferRepository repo;

    /**
     * Service under test with injected mocks.
     */
    @InjectMocks
    private OfferService service;

    /**
     * Configure the validity period before each test via reflection.
     */
    @BeforeEach
    void setUp() {
        // Set offer validity to 30 days for deterministic testing
        ReflectionTestUtils.setField(service, "validDays", 30);
    }

    /**
     * Test that creating an offer correctly calculates the total insured amount,
     * premium, and sets appropriate timestamps and status.
     */
    @Test
    void calculatesPremiumAndValidityOnCreate() {
        // Arrange: build a DTO with two loans totaling 2,000,000 SEK
        CreateOfferDto dto = new CreateOfferDto();
        dto.setPersonnummer("19800101-1234");
        dto.setManadskostnad(BigDecimal.valueOf(9500));
        dto.setLån(Arrays.asList(
            new LoanDto("Handelsbanken", BigDecimal.valueOf(1_200_000)),
            new LoanDto("SEB", BigDecimal.valueOf(800_000))
        ));

        // Mock save to return the same Offer instance provided
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: create the offer
        Offer result = service.createOffer(dto);

        // Assert: verify total and premium calculations
        BigDecimal expectedTotal = BigDecimal.valueOf(2_000_000);
        assertEquals(expectedTotal, result.getForsakratBelopp(),
            "Insured amount should match sum of loan amounts");
        assertEquals(expectedTotal.multiply(BigDecimal.valueOf(0.038)), result.getPremie(),
            "Premium should be 3.8% of the insured amount");
        assertEquals(OfferStatus.SKAPAD, result.getStatus(),
            "Status should be SKAPAD after creation");
        assertNotNull(result.getSkapad(), "Creation timestamp should be set");
        assertNotNull(result.getGiltigTill(), "Expiry timestamp should be set");
        assertTrue(result.getGiltigTill().isAfter(result.getSkapad()),
            "Expiry should be after creation");

        // Verify that the repository save method was invoked
        verify(repo).save(result);
    }

    /**
     * Test that accepting an offer before its expiry date succeeds,
     * updating status and acceptance timestamp.
     */
    @Test
    void acceptOfferBeforeExpirySucceeds() {
        // Arrange: existing offer valid for one more day
        Offer existing = new Offer();
        existing.setId("test-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));

        // Mock repository lookup and save
        when(repo.findById("test-id")).thenReturn(Optional.of(existing));
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act: accept the offer
        Offer accepted = service.acceptOffer("test-id");

        // Assert: status change and timestamp set
        assertEquals(OfferStatus.TECKNAD, accepted.getStatus(),
            "Status should change to TECKNAD on accept");
        assertNotNull(accepted.getAccepteradVid(),
            "Acceptance timestamp should be populated");
        verify(repo).save(accepted);
    }

    /**
     * Ensure that accepting an expired offer throws {@link OfferExpiredException}.
     */
    @Test
    void acceptOfferAfterExpiryThrowsException() {
        // Arrange: offer expired yesterday
        Offer existing = new Offer();
        existing.setId("expired-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().minusDays(1));

        when(repo.findById("expired-id")).thenReturn(Optional.of(existing));

        // Act & Assert: exception is thrown
        assertThrows(OfferExpiredException.class,
                     () -> service.acceptOffer("expired-id"),
                     "Expired offers should throw OfferExpiredException");
    }

    /**
     * Ensure that accepting a non-existent offer throws {@link OfferNotFoundException}.
     */
    @Test
    void acceptOfferNotFoundThrowsException() {
        // Arrange: no offer found in repo
        when(repo.findById("unknown")).thenReturn(Optional.empty());

        // Act & Assert: not found exception
        assertThrows(OfferNotFoundException.class,
                     () -> service.acceptOffer("unknown"),
                     "Non-existent offers should throw OfferNotFoundException");
    }

    /**
     * Test that updating an offer recalculates insured amount and premium,
     * and updates the personnummer correctly while preserving SKAPAD status.
     */
    @Test
    void updateOfferRecalculatesFields() {
        // Arrange: existing offer still valid
        Offer existing = new Offer();
        existing.setId("update-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));
        existing.setPersonnummer("old-ssn");

        when(repo.findById("update-id")).thenReturn(Optional.of(existing));
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        // New DTO with updated loan and personnummer
        UpdateOfferDto dto = new UpdateOfferDto();
        dto.setPersonnummer("new-ssn");
        dto.setManadskostnad(BigDecimal.valueOf(12000));
        dto.setLån(Collections.singletonList(
            new LoanDto("SBAB", BigDecimal.valueOf(1_000_000))
        ));

        // Act: perform update
        Offer updated = service.updateOffer("update-id", dto);

        // Assert: verify recalculated fields
        assertEquals("new-ssn", updated.getPersonnummer(),
            "Personnummer should be updated");
        assertEquals(BigDecimal.valueOf(1_000_000), updated.getForsakratBelopp(),
            "Insured amount should match updated loan amount");
        assertEquals(BigDecimal.valueOf(1_000_000).multiply(BigDecimal.valueOf(0.038)),
                     updated.getPremie(),
                     "Premium should be recalculated correctly");
        assertEquals(OfferStatus.SKAPAD, updated.getStatus(),
            "Status should remain SKAPAD after update");
        verify(repo).save(updated);
    }

    /**
     * Verify that updating an offer already in TECKNAD status throws {@link OfferAlreadyAcceptedException}.
     */
    @Test
    void updateOfferAlreadyAcceptedThrowsException() {
        // Arrange: offer already accepted
        Offer existing = new Offer();
        existing.setId("accepted-id");
        existing.setStatus(OfferStatus.TECKNAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));

        when(repo.findById("accepted-id")).thenReturn(Optional.of(existing));

        // Act & Assert: expected exception
        assertThrows(OfferAlreadyAcceptedException.class,
                     () -> service.updateOffer("accepted-id", new UpdateOfferDto()),
                     "Accepted offers should throw OfferAlreadyAcceptedException on update");
    }

    /**
     * Verify that updating an expired offer throws {@link OfferExpiredException}.
     */
    @Test
    void updateOfferAfterExpiryThrowsException() {
        // Arrange: offer expired yesterday
        Offer existing = new Offer();
        existing.setId("expired-update");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().minusDays(1));

        when(repo.findById("expired-update")).thenReturn(Optional.of(existing));

        // Act & Assert: expected exception
        assertThrows(OfferExpiredException.class,
                     () -> service.updateOffer("expired-update", new UpdateOfferDto()),
                     "Expired offers should throw OfferExpiredException on update");
    }
}
