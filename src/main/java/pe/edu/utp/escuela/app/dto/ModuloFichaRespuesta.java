package pe.edu.utp.escuela.app.dto;

import java.util.List;

public record ModuloFichaRespuesta(
        Long moduloId,
        String titulo,
        String descripcion,
        Integer orden,
        List<LeccionFichaRespuesta> lecciones) {

    public ModuloFichaRespuesta {
        lecciones = List.copyOf(lecciones);
    }
}
