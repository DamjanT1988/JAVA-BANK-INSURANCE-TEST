package com.example.gofido.controller;

import com.example.gofido.dto.CreateOfferDto;
import com.example.gofido.dto.LoanDto;
import com.example.gofido.dto.OfferResponseDto;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.gofido.dto.UpdateOfferDto;
import com.example.gofido.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/offer")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService svc;

    @PostMapping
    public ResponseEntity<OfferResponseDto> create(@RequestBody CreateOfferDto dto) {
        var o = svc.createOffer(dto);
        return ResponseEntity.ok(toDto(o));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferResponseDto> update(
            @PathVariable String id,
            @RequestBody UpdateOfferDto dto) {
        var o = svc.updateOffer(id, dto);
        return ResponseEntity.ok(toDto(o));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<OfferResponseDto> accept(@PathVariable String id) {
        var o = svc.acceptOffer(id);
        return ResponseEntity.ok(toDto(o));
    }

    private OfferResponseDto toDto(com.example.gofido.domain.Offer o) {
        return new OfferResponseDto(
            o.getId(),
            o.getPersonnummer(),
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
