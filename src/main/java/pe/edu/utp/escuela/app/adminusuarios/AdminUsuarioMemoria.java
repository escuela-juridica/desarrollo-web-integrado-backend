package pe.edu.utp.escuela.app.adminusuarios;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

/** Fila en memoria (ver package-info): no es una entidad JPA, no se persiste. */
@Getter
@Setter
public class AdminUsuarioMemoria {
    private Long usuarioId;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String telefono;
    private String documentoIdentidad;
    private RolUsuarioAdmin rolPrincipal;
    private OrigenUsuarioAdmin origenRegistro;
    private boolean activo;
    private CondicionCuentaAdmin condicion;
    private OtorgadoPorAdmin otorgadoPor;
    private Instant creadoEn;
    private String concedidoPorNombre;

    public String nombreCompleto() {
        StringBuilder nombre = new StringBuilder(nombres).append(' ').append(apellidoPaterno);
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) {
            nombre.append(' ').append(apellidoMaterno);
        }
        return nombre.toString();
    }
}
