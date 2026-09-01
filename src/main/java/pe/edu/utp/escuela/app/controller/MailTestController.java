package pe.edu.utp.escuela.app.controller;

import jakarta.validation.Valid;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.escuela.app.dto.MailTestRequest;
import pe.edu.utp.escuela.app.dto.MailTestResponse;
import pe.edu.utp.escuela.app.mail.HtmlMailMessage;
import pe.edu.utp.escuela.app.mail.MailService;

@Profile({"local", "dev"})
@RestController
@RequestMapping("/api/testing/mail")
@RequiredArgsConstructor
public class MailTestController {

    private static final String TEMPLATE = "mail/test-mail.html";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z");

    private final MailService mailService;
    private final Clock clock;

    @PostMapping
    public ResponseEntity<MailTestResponse> send(@Valid @RequestBody MailTestRequest request) {
        ZonedDateTime now = ZonedDateTime.now(clock);
        mailService.sendHtml(HtmlMailMessage.to(
                request.recipient(),
                request.subject(),
                TEMPLATE,
                Map.of(
                        "name", request.name().strip(),
                        "message", request.message().strip(),
                        "sentAt", DATE_FORMAT.format(now),
                        "year", Integer.toString(now.getYear()))));

        return ResponseEntity.ok(new MailTestResponse(
                "SENT", request.recipient().strip(), now.toInstant()));
    }
}
