package pe.edu.utp.escuela.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pe.edu.utp.escuela.app.dto.MailTestRequest;
import pe.edu.utp.escuela.app.mail.HtmlMailMessage;
import pe.edu.utp.escuela.app.mail.MailService;

class MailTestControllerTests {

    @Test
    void sendsToRecipientProvidedInRequest() {
        MailService mailService = mock(MailService.class);
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-31T20:00:00Z"), ZoneId.of("America/Lima"));
        MailTestController controller = new MailTestController(mailService, clock);

        var response = controller.send(new MailTestRequest(
                "destinatario@correo.pe",
                "Prueba ESEJUR",
                "Enrique",
                "Validación del servicio"));

        ArgumentCaptor<HtmlMailMessage> captor = ArgumentCaptor.forClass(HtmlMailMessage.class);
        verify(mailService).sendHtml(captor.capture());
        assertEquals("destinatario@correo.pe", captor.getValue().recipients().getFirst());
        assertEquals("SENT", response.getBody().status());
    }
}
