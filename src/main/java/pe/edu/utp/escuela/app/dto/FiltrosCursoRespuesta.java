package pe.edu.utp.escuela.app.dto;

import java.util.List;

public record FiltrosCursoRespuesta(
        List<OpcionFiltroRespuesta> tipos,
        List<OpcionFiltroRespuesta> categorias) {

    public FiltrosCursoRespuesta {
        tipos = List.copyOf(tipos);
        categorias = List.copyOf(categorias);
    }
}
