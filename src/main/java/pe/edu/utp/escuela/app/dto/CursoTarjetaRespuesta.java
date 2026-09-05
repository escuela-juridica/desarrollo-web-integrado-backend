package pe.edu.utp.escuela.app.dto;

import java.math.BigDecimal;
import java.util.List;

public record CursoTarjetaRespuesta(
        String urlAmigable,
        String titulo,
        String descripcion,
        String imagenPortadaUrl,
        String modalidad,
        String tipoVenta,
        String tipoCursoCodigo,
        String tipoCursoNombre,
        String categoriaCodigo,
        String categoriaNombre,
        boolean destacado,
        BigDecimal horasAcademicas,
        List<DocenteBreveRespuesta> docentes,
        EstadoComercialRespuesta estadoComercial) {

    public CursoTarjetaRespuesta {
        docentes = List.copyOf(docentes);
    }
}
