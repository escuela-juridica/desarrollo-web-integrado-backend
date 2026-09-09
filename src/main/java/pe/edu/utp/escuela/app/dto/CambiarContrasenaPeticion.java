package pe.edu.utp.escuela.app.dto;

import jakarta.validation.constraints.NotBlank;

public record CambiarContrasenaPeticion(
        @NotBlank String contrasenaActual,
        @NotBlank String contrasenaNueva,
        @NotBlank String confirmacion) {
    @Override
    public String toString() {
        return "CambiarContrasenaPeticion[datos sensibles omitidos]";
    }
}
