package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class PendingEmailVerificationException extends BusinessException {
    public PendingEmailVerificationException() {
        super(HttpStatus.FORBIDDEN, "PENDING_EMAIL_VERIFICATION", "Debes verificar tu correo antes de ingresar");
    }
}
