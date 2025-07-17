package main.java.com.example.gofido.controller;

import com.example.gofido.repository.OfferRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final OfferRepository repo;

    public StatsController(OfferRepository repo) { this.repo = repo; }

    @GetMapping("/conversion")
    public Map<String, Object> conversion() {
        long total = repo.count();
        long accepted = repo.countByStatusAndAccepteradVidBefore(...); // använd giltigTid
        double rate = total == 0 ? 0 : accepted * 100.0 / total;
        return Map.of(
            "totalaOfferter", total,
            "accepteradeOfferter", accepted,
            "konverteringsgrad", rate,
            "tidsintervall", validDays + " dagar"
        );
    }
}
