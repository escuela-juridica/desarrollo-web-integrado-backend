package pe.edu.utp.escuela.app.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pe.edu.utp.escuela.app.exception.MailDeliveryException;

@Slf4j
@Service
public class SmtpMailService implements MailService {

    private final JavaMailSender mailSender;
    private final MailTemplateRenderer templateRenderer;
    private final String sender;

    public SmtpMailService(
            JavaMailSender mailSender,
            MailTemplateRenderer templateRenderer,
            @Value("${spring.mail.username}") String sender) {
        this.mailSender = mailSender;
        this.templateRenderer = templateRenderer;
        this.sender = sender;
    }

    @Override
    public void sendHtml(HtmlMailMessage message) {
        validate(message);
        String html = templateRenderer.render(message.templatePath(), message.fields());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, false, StandardCharsets.UTF_8.name());
            helper.setValidateAddresses(true);
            helper.setFrom(sender);
            helper.setTo(message.recipients().toArray(String[]::new));
            helper.setSubject(message.subject().strip());
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.info("Correo enviado con plantilla {} a {} destinatario(s)",
                    message.templatePath(), message.recipients().size());
        } catch (MessagingException | MailException exception) {
            log.error("No se pudo enviar el correo con plantilla {}", message.templatePath(), exception);
            throw new MailDeliveryException();
        }
    }

    private void validate(HtmlMailMessage message) {
        List<String> recipients = message.recipients();
        if (recipients.isEmpty() || recipients.stream().anyMatch(this::isBlank)) {
            throw new IllegalArgumentException("Debe existir al menos un destinatario");
        }
        if (isBlank(message.subject())) {
            throw new IllegalArgumentException("El asunto es obligatorio");
        }
        if (message.subject().contains("\r") || message.subject().contains("\n")) {
            throw new IllegalArgumentException("El asunto no puede contener saltos de línea");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
