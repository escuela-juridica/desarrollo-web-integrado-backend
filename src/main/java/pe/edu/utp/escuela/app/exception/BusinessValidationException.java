package pe.edu.utp.escuela.app.exception;

import org.springframework.http.HttpStatus;

public class BusinessValidationException extends BusinessException {
    public BusinessValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, "BUSINESS_VALIDATION_ERROR", message);
    }
}
