package pe.edu.utp.escuela.app.mail;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class MailTemplateRendererTests {

    private final MailTemplateRenderer renderer = new MailTemplateRenderer();

    @Test
    void loadsResourceAndEscapesFields() {
        String html = renderer.render("mail/test-mail.html", Map.of(
                "name", "Ana <script>",
                "message", "Prueba & confirmación",
                "sentAt", "31/08/2026 20:00 PET",
                "year", "2026"));

        assertTrue(html.contains("Ana &lt;script&gt;"));
        assertTrue(html.contains("Prueba &amp; confirmación"));
        assertTrue(html.contains("31/08/2026 20:00 PET"));
    }

    @Test
    void rejectsMissingFieldsAndUnsafePaths() {
        assertThrows(IllegalArgumentException.class,
                () -> renderer.render("mail/test-mail.html", Map.of()));
        assertThrows(IllegalArgumentException.class,
                () -> renderer.render("../application.yml", Map.of()));
    }
}
