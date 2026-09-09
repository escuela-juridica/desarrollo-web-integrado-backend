package pe.edu.utp.escuela.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarRecuperacionPeticion(@NotBlank @Email String correo) {
}
