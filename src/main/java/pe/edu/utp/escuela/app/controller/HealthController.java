package pe.edu.utp.escuela.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final Clock clock;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "timestamp", Instant.now(clock).toString()
        );
    }

}
