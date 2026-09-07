package pe.edu.utp.escuela.app.dto;

import java.util.List;

public record VistaPreviaRespuesta(
        Long leccionId,
        List<RecursoVistaPreviaRespuesta> materiales) {

    public VistaPreviaRespuesta {
        materiales = List.copyOf(materiales);
    }
}
