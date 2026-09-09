package pe.edu.utp.escuela.app.adminusuarios;

import java.time.Instant;

public record UsuarioAdminRespuesta(
        Long usuarioId,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String nombreCompleto,
        String correo,
        String telefono,
        String documentoIdentidad,
        RolUsuarioAdmin rolPrincipal,
        OrigenUsuarioAdmin origenRegistro,
        boolean activo,
        CondicionCuentaAdmin condicion,
        OtorgadoPorAdmin otorgadoPor,
        Instant creadoEn,
        String concedidoPorNombre) {

    public static UsuarioAdminRespuesta de(AdminUsuarioMemoria u) {
        return new UsuarioAdminRespuesta(
                u.getUsuarioId(),
                u.getNombres(),
                u.getApellidoPaterno(),
                u.getApellidoMaterno(),
                u.nombreCompleto(),
                u.getCorreo(),
                u.getTelefono(),
                u.getDocumentoIdentidad(),
                u.getRolPrincipal(),
                u.getOrigenRegistro(),
                u.isActivo(),
                u.getCondicion(),
                u.getOtorgadoPor(),
                u.getCreadoEn(),
                u.getConcedidoPorNombre());
    }
}
