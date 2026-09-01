package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Debes iniciar sesión");
    }
}
