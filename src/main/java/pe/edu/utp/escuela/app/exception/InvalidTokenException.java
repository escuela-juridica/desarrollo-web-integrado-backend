package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException() {
        super(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "El enlace no es válido o ya no está disponible");
    }
}
