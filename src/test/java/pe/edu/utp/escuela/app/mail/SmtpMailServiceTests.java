package pe.edu.utp.escuela.app.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import pe.edu.utp.escuela.app.exception.MailDeliveryException;

class SmtpMailServiceTests {

    private JavaMailSender mailSender;
    private SmtpMailService service;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        service = new SmtpMailService(
                mailSender, new MailTemplateRenderer(), "notificaciones@esejur.pe");
    }

    @Test
    void buildsAndSendsHtmlMessageToRequestedRecipient() throws Exception {
        service.sendHtml(HtmlMailMessage.to(
                "destinatario@correo.pe",
                "Prueba de correo",
                "mail/test-mail.html",
                Map.of(
                        "name", "Enrique",
                        "message", "El servicio funciona",
                        "sentAt", "31/08/2026 20:00 PET",
                        "year", "2026")));

        verify(mailSender).send(mimeMessage);
        mimeMessage.saveChanges();
        assertEquals("Prueba de correo", mimeMessage.getSubject());
        assertEquals("destinatario@correo.pe", mimeMessage.getAllRecipients()[0].toString());
        assertTrue(mimeMessage.getContentType().toLowerCase().contains("text/html"));
        assertTrue(mimeMessage.getContent().toString().contains("El servicio funciona"));
    }

    @Test
    void convertsProviderFailureIntoSafeBusinessError() {
        org.mockito.Mockito.doThrow(new MailSendException("SMTP caído"))
                .when(mailSender).send(mimeMessage);

        HtmlMailMessage message = HtmlMailMessage.to(
                "destinatario@correo.pe",
                "Prueba",
                "mail/test-mail.html",
                Map.of(
                        "name", "Enrique",
                        "message", "Prueba",
                        "sentAt", "31/08/2026 20:00 PET",
                        "year", "2026"));

        assertThrows(MailDeliveryException.class, () -> service.sendHtml(message));
    }
}
