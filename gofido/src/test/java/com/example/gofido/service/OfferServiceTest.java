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
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository repo;

    @InjectMocks
    private OfferService service;

    @BeforeEach
    void setUp() {
        // Sätter giltighetsperioden till 30 dagar i testerna
        ReflectionTestUtils.setField(service, "validDays", 30);
    }

    @Test
    void calculatesPremiumAndValidityOnCreate() {
        CreateOfferDto dto = new CreateOfferDto();
        dto.setPersonnummer("19800101-1234");
        dto.setManadskostnad(BigDecimal.valueOf(9500));
        dto.setLån(Arrays.asList(
            new LoanDto("Handelsbanken", BigDecimal.valueOf(1_200_000)),
            new LoanDto("SEB", BigDecimal.valueOf(800_000))
        ));

        // Mocka save så att den returnerar samma objekt som skickas in
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        Offer result = service.createOffer(dto);

        BigDecimal expectedTotal = BigDecimal.valueOf(2_000_000);
        assertEquals(expectedTotal, result.getForsakratBelopp(), "Försäkrat belopp ska vara summa av lån");
        assertEquals(expectedTotal.multiply(BigDecimal.valueOf(0.038)), result.getPremie(), "Premien ska vara 3.8%");
        assertEquals(OfferStatus.SKAPAD, result.getStatus(), "Status ska vara SKAPAD");
        assertNotNull(result.getSkapad(), "Skapad-tid ska vara satt");
        assertNotNull(result.getGiltigTill(), "GiltigTill-tid ska vara satt");
        assertTrue(result.getGiltigTill().isAfter(result.getSkapad()), "GiltigTill ska vara efter Skapad");
        verify(repo).save(result);
    }

    @Test
    void acceptOfferBeforeExpirySucceeds() {
        Offer existing = new Offer();
        existing.setId("test-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));

        when(repo.findById("test-id")).thenReturn(Optional.of(existing));
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        Offer accepted = service.acceptOffer("test-id");

        assertEquals(OfferStatus.TECKNAD, accepted.getStatus(), "Status ska bli TECKNAD");
        assertNotNull(accepted.getAccepteradVid(), "AccepteradVid ska vara satt");
        verify(repo).save(accepted);
    }

    @Test
    void acceptOfferAfterExpiryThrowsException() {
        Offer existing = new Offer();
        existing.setId("expired-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().minusDays(1));

        when(repo.findById("expired-id")).thenReturn(Optional.of(existing));

        assertThrows(OfferExpiredException.class,
                     () -> service.acceptOffer("expired-id"),
                     "OfferExpiredException ska kastas om offerten är utgången");
    }

    @Test
    void acceptOfferNotFoundThrowsException() {
        when(repo.findById("unknown")).thenReturn(Optional.empty());
        assertThrows(OfferNotFoundException.class,
                     () -> service.acceptOffer("unknown"),
                     "OfferNotFoundException ska kastas om offerten inte finns");
    }

    @Test
    void updateOfferRecalculatesFields() {
        Offer existing = new Offer();
        existing.setId("update-id");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));
        existing.setPersonnummer("old-ssn");

        when(repo.findById("update-id")).thenReturn(Optional.of(existing));
        when(repo.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOfferDto dto = new UpdateOfferDto();
        dto.setPersonnummer("new-ssn");
        dto.setManadskostnad(BigDecimal.valueOf(12000));
        dto.setLån(Collections.singletonList(
            new LoanDto("SBAB", BigDecimal.valueOf(1_000_000))
        ));

        Offer updated = service.updateOffer("update-id", dto);

        assertEquals("new-ssn", updated.getPersonnummer(), "Personnummer ska uppdateras");
        assertEquals(BigDecimal.valueOf(1_000_000), updated.getForsakratBelopp(), "Försäkrat belopp ska uppdateras");
        assertEquals(BigDecimal.valueOf(1_000_000).multiply(BigDecimal.valueOf(0.038)),
                     updated.getPremie(),
                     "Premien ska omräknas korrekt");
        assertEquals(OfferStatus.SKAPAD, updated.getStatus(), "Status ska fortfarande vara SKAPAD");
        verify(repo).save(updated);
    }

    @Test
    void updateOfferAlreadyAcceptedThrowsException() {
        Offer existing = new Offer();
        existing.setId("accepted-id");
        existing.setStatus(OfferStatus.TECKNAD);
        existing.setGiltigTill(LocalDateTime.now().plusDays(1));

        when(repo.findById("accepted-id")).thenReturn(Optional.of(existing));

        assertThrows(OfferAlreadyAcceptedException.class,
                     () -> service.updateOffer("accepted-id", new UpdateOfferDto()),
                     "OfferAlreadyAcceptedException ska kastas om offerten redan är accepterad");
    }

    @Test
    void updateOfferAfterExpiryThrowsException() {
        Offer existing = new Offer();
        existing.setId("expired-update");
        existing.setStatus(OfferStatus.SKAPAD);
        existing.setGiltigTill(LocalDateTime.now().minusDays(1));

        when(repo.findById("expired-update")).thenReturn(Optional.of(existing));

        assertThrows(OfferExpiredException.class,
                     () -> service.updateOffer("expired-update", new UpdateOfferDto()),
                     "OfferExpiredException ska kastas om offerten är utgången vid uppdatering");
    }
}
