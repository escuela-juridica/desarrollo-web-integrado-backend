package pe.edu.utp.escuela.app.adminusuarios;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioAdminPeticion(@NotNull Boolean activo) {
}
