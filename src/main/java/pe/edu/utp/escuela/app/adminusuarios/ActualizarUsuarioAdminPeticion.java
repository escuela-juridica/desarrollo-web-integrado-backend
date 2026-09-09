package pe.edu.utp.escuela.app.adminusuarios;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** No incluye correo, rol ni estado: eso no se edita desde aquí (correo es la identidad de la
 * cuenta, el rol y el estado tienen sus propias acciones dedicadas). */
public record ActualizarUsuarioAdminPeticion(
        @NotBlank @Size(max = 120) String nombres,
        @NotBlank @Size(max = 80) String apellidoPaterno,
        @Size(max = 80) String apellidoMaterno,
        @Size(max = 30) String telefono,
        @Size(max = 30) String documentoIdentidad) {
}
