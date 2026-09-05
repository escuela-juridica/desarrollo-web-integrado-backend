package pe.edu.utp.escuela.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.utp.escuela.app.dto.CursoTarjetaRespuesta;
import pe.edu.utp.escuela.app.dto.FiltrosCursoRespuesta;
import pe.edu.utp.escuela.app.dto.PageResponse;
import pe.edu.utp.escuela.app.service.CatalogoServicio;

@Tag(
        name = "Catálogo público",
        description = "Consulta pública de cursos publicados. No requiere sesión y nunca expone "
                + "matrículas, alumnos, materiales protegidos ni certificados.")
@RestController
@RequestMapping("/api/publico/cursos")
@RequiredArgsConstructor
public class CatalogoControlador {

    private final CatalogoServicio catalogoServicio;

    @Operation(
            summary = "Listar cursos del catálogo",
            description = "Devuelve una página de tarjetas de curso según la búsqueda y los filtros "
                    + "aplicados. Excluye borradores y cancelados, y exige publicación real. "
                    + "Ordena los destacados primero y, dentro de cada grupo, inicio inmediato, "
                    + "fechas futuras más próximas y luego cursos en progreso. El estado comercial "
                    + "y los precios vienen calculados en América/Lima.")
    @ApiResponse(responseCode = "200", description = "Página de cursos; lista vacía si nada coincide")
    @GetMapping
    public PageResponse<CursoTarjetaRespuesta> listar(
            @Parameter(description = "Texto libre buscado en el título y la descripción del curso",
                    example = "registral")
            @RequestParam(required = false) String texto,
            @Parameter(description = "Código estable del tipo de curso. Ausente o 'TODOS' no filtra",
                    example = "DIPLOMADO")
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Código estable de la categoría temática. Ausente o 'TODOS' no filtra",
                    example = "DERECHO_REGISTRAL")
            @RequestParam(required = false) String categoria,
            @Parameter(description = "Página solicitada, empezando en cero", example = "0")
            @RequestParam(defaultValue = "0") int pagina,
            @Parameter(description = "Cantidad de cursos por página; se acota entre 1 y 50",
                    example = "9")
            @RequestParam(defaultValue = "9") int tamano) {
        return catalogoServicio.listar(texto, tipo, categoria, pagina, tamano);
    }

    @Operation(
            summary = "Consultar los filtros disponibles",
            description = "Devuelve los tipos de curso y las categorías temáticas activos, ordenados "
                    + "para presentarlos en los selectores. La modalidad no es un filtro.")
    @ApiResponse(responseCode = "200", description = "Tipos y categorías activos y ordenados")
    @GetMapping("/filtros")
    public FiltrosCursoRespuesta filtros() {
        return catalogoServicio.filtros();
    }
}
