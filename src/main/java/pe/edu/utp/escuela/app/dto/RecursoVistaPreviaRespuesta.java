package pe.edu.utp.escuela.app.dto;

public record RecursoVistaPreviaRespuesta(
        Long materialLeccionId,
        String titulo,
        Integer orden,
        String tipoRecurso,
        String origen,
        String referencia,
        String nombreArchivo,
        String tipoMime,
        Integer duracionSegundos,
        boolean permiteDescarga) {
}
