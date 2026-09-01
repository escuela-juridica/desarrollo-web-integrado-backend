package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class OperationNotAllowedException extends BusinessException {
    public OperationNotAllowedException(String message) {
        super(HttpStatus.CONFLICT, "OPERATION_NOT_ALLOWED", message);
    }
}
