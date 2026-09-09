package pe.edu.utp.escuela.app.dto;

/** Siempre el mismo contenido, exista o no la cuenta: la neutralidad es la respuesta completa. */
public record RecuperacionSolicitadaRespuesta(String mensaje) {

    private static final String MENSAJE_NEUTRAL =
            "Si existe una cuenta asociada a ese correo, recibirás un enlace de recuperación.";

    public static RecuperacionSolicitadaRespuesta neutral() {
        return new RecuperacionSolicitadaRespuesta(MENSAJE_NEUTRAL);
    }
}
