package main.java.com.example.gofido.service;

import com.example.gofido.domain.Loan;
import com.example.gofido.domain.Offer;
import com.example.gofido.domain.OfferStatus;
import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.LoanDto;
import com.example.gofido.exception.OfferExpiredException;
import com.example.gofido.exception.OfferNotFoundException;
import com.example.gofido.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final OfferRepository repo;

    @Value("${offer.valid-days}")
    private int validDays;

    public Offer createOffer(CreateOfferDto dto) {
        Offer o = new Offer();
        o.setPersonnummer(dto.getPersonnummer());
        o.setLoans(dto.getLån().stream()
            .map(l -> new Loan(l.getBank(), l.getBelopp()))
            .collect(Collectors.toList()));
        o.setManadskostnad(dto.getManadskostnad());

        BigDecimal total = o.getLoans().stream()
            .map(Loan::getBelopp)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        o.setForsakratBelopp(total);
        o.setPremie(total.multiply(BigDecimal.valueOf(0.038)));

        o.setStatus(OfferStatus.SKAPAD);
        o.setSkapad(LocalDateTime.now());
        o.setGiltigTill(o.getSkapad().plusDays(validDays));

        return repo.save(o);
    }

    public Offer acceptOffer(String id) {
        Offer o = repo.findById(id).orElseThrow(() -> new OfferNotFoundException(id));
        if (LocalDateTime.now().isAfter(o.getGiltigTill())) {
            throw new OfferExpiredException(id);
        }
        o.setStatus(OfferStatus.TECKNAD);
        o.setAccepteradVid(LocalDateTime.now());
        return repo.save(o);
    }

    // (Lägg till updateOffer, stats-metoder på liknande sätt)
}
