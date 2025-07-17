package main.java.com.example.gofido.controller;

import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.OfferResponseDto;
import com.example.gofido.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService svc;

    @PostMapping
    public ResponseEntity<OfferResponseDto> create(@RequestBody CreateOfferDto dto) {
        var o = svc.createOffer(dto);
        // mappa Offer → OfferResponseDto
        return ResponseEntity.ok(toDto(o));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<OfferResponseDto> accept(@PathVariable String id) {
        var o = svc.acceptOffer(id);
        return ResponseEntity.ok(toDto(o));
    }

    // TODO: PUT /offer/{id}, GET /stats/conversion

    private OfferResponseDto toDto(com.example.gofido.domain.Offer o) {
        return new OfferResponseDto(
            o.getId(),
            o.getPersonnummer(),
            o.getLoans().stream()
                .map(l -> new com.example.gofido.dto.LoanDto(l.getBank(), l.getBelopp()))
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
