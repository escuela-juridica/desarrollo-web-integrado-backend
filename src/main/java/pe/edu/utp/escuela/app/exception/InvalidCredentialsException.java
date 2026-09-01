package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BusinessException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Correo o contraseña incorrectos");
    }
}
