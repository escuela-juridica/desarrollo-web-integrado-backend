package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class InactiveAccountException extends BusinessException {
    public InactiveAccountException() {
        super(HttpStatus.FORBIDDEN, "INACTIVE_ACCOUNT", "La cuenta se encuentra deshabilitada");
    }
}
