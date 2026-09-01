package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class MailDeliveryException extends BusinessException {
    public MailDeliveryException() {
        super(HttpStatus.BAD_GATEWAY, "MAIL_DELIVERY_ERROR",
                "No se pudo entregar el correo en este momento");
    }
}
