package pe.edu.utp.escuela.app.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import pe.edu.utp.escuela.app.entity.*;
import pe.edu.utp.escuela.app.exception.MailDeliveryException;
import pe.edu.utp.escuela.app.mail.*;
import pe.edu.utp.escuela.app.repository.CodigoVerificacionRepositorio;

@Service @RequiredArgsConstructor
public class CodigoVerificacionServicio {
    private final CodigoVerificacionRepositorio codigos;
    private final PasswordEncoder encoder;
    private final MailService mail;
    private final Clock clock;
    private final SecureRandom random = new SecureRandom();

    /** Debe participar en la misma transacción que crea la cuenta. */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean emitirPara(Usuario usuario) {
        codigos.invalidarAnteriores(usuario.getId(), clock.instant());
        String visible = String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
        var codigo = new CodigoVerificacionCorreo();
        codigo.setUsuario(usuario);
        codigo.setCodigoHash(encoder.encode(visible));
        codigo.setSolicitadoEn(clock.instant());
        codigo.setModificadoEn(clock.instant());
        codigos.saveAndFlush(codigo);
        try {
            mail.sendHtml(new HtmlMailMessage(List.of(usuario.getCorreo()),
                    "Bienvenido a ESEJUR: verifica tu correo", "mail/verification-code.html",
                    Map.of("nombre", usuario.getPersona().getNombres(), "codigo", visible)));
            codigo.setEstadoEnvio("ENVIADO");
        } catch (MailDeliveryException exception) {
            codigo.setEstadoEnvio("ERROR");
            codigo.setInvalidadoEn(clock.instant());
        }
        codigo.setModificadoEn(clock.instant());
        codigos.saveAndFlush(codigo);
        return "ENVIADO".equals(codigo.getEstadoEnvio());
    }
}
