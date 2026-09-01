package pe.edu.utp.escuela.app.dto;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldErrorResponse> fieldErrors) {

    public ApiErrorResponse {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }
}
