package pe.edu.utp.escuela.app.mail;

import java.util.List;
import java.util.Map;

public record HtmlMailMessage(
        List<String> recipients,
        String subject,
        String templatePath,
        Map<String, ?> fields) {

    public HtmlMailMessage {
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
        fields = fields == null ? Map.of() : Map.copyOf(fields);
    }

    public static HtmlMailMessage to(
            String recipient, String subject, String templatePath, Map<String, ?> fields) {
        return new HtmlMailMessage(List.of(recipient), subject, templatePath, fields);
    }
}
