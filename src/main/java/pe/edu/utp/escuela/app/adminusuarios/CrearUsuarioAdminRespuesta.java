package pe.edu.utp.escuela.app.adminusuarios;

/** {@code reutilizada}: true si el correo ya existía (se reutilizó la cuenta y no se generó
 * contraseña temporal). {@code contrasenaTemporal}: valor de demostración fijo, no se envía
 * ningún correo real — ver el comentario del paquete. */
public record CrearUsuarioAdminRespuesta(
        UsuarioAdminRespuesta usuario,
        boolean reutilizada,
        String contrasenaTemporal) {
}
