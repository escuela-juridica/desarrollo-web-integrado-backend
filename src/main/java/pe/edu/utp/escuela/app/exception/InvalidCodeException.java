package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidCodeException extends BusinessException {
    public InvalidCodeException() {
        super(HttpStatus.BAD_REQUEST, "INVALID_CODE", "El código ingresado no es válido");
    }
}
