package pe.edu.utp.escuela.app.dto;

public record SesionAccesoRespuesta(
        Long usuarioId,
        String nombreCompleto,
        String correo,
        String rolPrincipal,
        boolean requiereCambioContrasena) {
}
