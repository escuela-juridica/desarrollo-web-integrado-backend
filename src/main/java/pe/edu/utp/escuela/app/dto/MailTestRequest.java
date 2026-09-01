package pe.edu.utp.escuela.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MailTestRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        @Email(message = "El destinatario debe ser un correo válido")
        @Size(max = 254, message = "El destinatario no puede superar 254 caracteres")
        String recipient,

        @NotBlank(message = "El asunto es obligatorio")
        @Size(max = 180, message = "El asunto no puede superar 180 caracteres")
        String subject,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 160, message = "El nombre no puede superar 160 caracteres")
        String name,

        @NotBlank(message = "El mensaje es obligatorio")
        @Size(max = 2000, message = "El mensaje no puede superar 2000 caracteres")
        String message) {
}
