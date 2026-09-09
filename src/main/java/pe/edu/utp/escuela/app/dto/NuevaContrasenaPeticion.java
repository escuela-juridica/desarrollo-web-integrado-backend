package pe.edu.utp.escuela.app.dto;

import jakarta.validation.constraints.NotBlank;

public record NuevaContrasenaPeticion(
        @NotBlank String contrasena,
        @NotBlank String confirmacion) {
}
