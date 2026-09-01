package pe.edu.utp.escuela.app.dto;

import java.time.Instant;

public record MailTestResponse(String status, String recipient, Instant sentAt) {
}
