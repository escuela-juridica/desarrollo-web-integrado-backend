package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pe.edu.utp.escuela.app.dto.CursoTarjetaFila;
import pe.edu.utp.escuela.app.dto.CursoTarjetaRespuesta;
import pe.edu.utp.escuela.app.dto.DocenteCursoFila;
import pe.edu.utp.escuela.app.dto.FiltrosCursoRespuesta;
import pe.edu.utp.escuela.app.dto.PageResponse;
import pe.edu.utp.escuela.app.entity.CategoriaTematica;
import pe.edu.utp.escuela.app.entity.TipoCurso;
import pe.edu.utp.escuela.app.repository.CategoriaTematicaRepositorio;
import pe.edu.utp.escuela.app.repository.CursoDocenteRepositorio;
import pe.edu.utp.escuela.app.repository.CursoRepositorio;
import pe.edu.utp.escuela.app.repository.MatriculaRepositorio;
import pe.edu.utp.escuela.app.repository.TipoCursoRepositorio;
import pe.edu.utp.escuela.app.util.TextNormalizer;

@ExtendWith(MockitoExtension.class)
class CatalogoServicioTests {

    @Mock private CursoRepositorio cursoRepositorio;
    @Mock private MatriculaRepositorio matriculaRepositorio;
    @Mock private CursoDocenteRepositorio cursoDocenteRepositorio;
    @Mock private TipoCursoRepositorio tipoCursoRepositorio;
    @Mock private CategoriaTematicaRepositorio categoriaTematicaRepositorio;

    private CatalogoServicio servicio;
    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(Instant.parse("2026-09-07T12:00:00Z"), ZoneId.of("America/Lima"));
        servicio = new CatalogoServicio(
                cursoRepositorio,
                matriculaRepositorio,
                cursoDocenteRepositorio,
                tipoCursoRepositorio,
                categoriaTematicaRepositorio,
                new CourseCommercialStatusService(clock),
                new TextNormalizer(),
                clock);
    }

    @Test
    void listarSinResultadosNoConsultaMatriculasNiDocentes() {
        when(cursoRepositorio.buscarPublicados(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 9), 0));

        PageResponse<CursoTarjetaRespuesta> resultado = servicio.listar(null, null, null, 0, 9);

        assertTrue(resultado.items().isEmpty());
        assertEquals(0, resultado.totalElements());
        verify(matriculaRepositorio, never()).contarActivasPorCurso(any());
        verify(cursoDocenteRepositorio, never()).buscarDocentesDeCursos(any());
    }

    @Test
    void listarNormalizaTextoYFiltrosAusentesComoCadenaVacia() {
        when(cursoRepositorio.buscarPublicados(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 9), 0));

        servicio.listar(null, null, "TODOS", 0, 9);

        verify(cursoRepositorio).buscarPublicados(eq(""), eq(""), eq(""), eq(LocalDate.now(clock)), any());
    }

    @Test
    void listarNormalizaTextoATrimYMinusculas() {
        when(cursoRepositorio.buscarPublicados(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 9), 0));

        servicio.listar("  REGISTRAL  ", "diplomado", "  ", 0, 9);

        verify(cursoRepositorio)
                .buscarPublicados(eq("registral"), eq("DIPLOMADO"), eq(""), eq(LocalDate.now(clock)), any());
    }

    @Test
    void listarAcotaElTamanoDePaginaEntreUnoYCincuenta() {
        when(cursoRepositorio.buscarPublicados(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 1), 0));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        servicio.listar(null, null, null, 0, 0);

        verify(cursoRepositorio).buscarPublicados(any(), any(), any(), any(), captor.capture());
        assertEquals(1, captor.getValue().getPageSize());

        servicio.listar(null, null, null, 0, 1000);

        verify(cursoRepositorio, org.mockito.Mockito.times(2))
                .buscarPublicados(any(), any(), any(), any(), captor.capture());
        assertEquals(50, captor.getValue().getPageSize());
    }

    @Test
    void listarMapeaEstadoComercialYOrdenaDocentesPorOrden() {
        CursoTarjetaFila fila = new CursoTarjetaFila(
                1L, "registral", "Diplomado Registral", "Descripción", "portada.jpg",
                "VIRTUAL", "GRATUITO", true, BigDecimal.ZERO, null, null, null,
                null, null, null, null, new BigDecimal("120.00"),
                "DIPLOMADO", "Diplomado", "DERECHO_REGISTRAL", "Derecho Registral", "PUBLICADO");
        when(cursoRepositorio.buscarPublicados(any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(fila), PageRequest.of(0, 9), 1));
        when(matriculaRepositorio.contarActivas(List.of(1L)))
                .thenReturn(Map.of(1L, 3L));
        when(cursoDocenteRepositorio.buscarDocentesDeCursos(List.of(1L)))
                .thenReturn(List.of(
                        new DocenteCursoFila(1L, 20L, "Ariana", "Lazaro", "Maza", null, "Docente", 2),
                        new DocenteCursoFila(1L, 10L, "Miguel", "Saldivar", "Davalos", null, "Docente", 1)));

        PageResponse<CursoTarjetaRespuesta> resultado = servicio.listar(null, null, null, 0, 9);

        assertEquals(1, resultado.items().size());
        CursoTarjetaRespuesta curso = resultado.items().get(0);
        assertEquals("registral", curso.urlAmigable());
        assertEquals("IMMEDIATE_START", curso.estadoComercial().codigo());
        assertEquals("ACCESS_FREE", curso.estadoComercial().accion());
        assertEquals(2, curso.docentes().size());
        assertEquals("Miguel Saldivar Davalos", curso.docentes().get(0).nombreCompleto());
        assertEquals("Ariana Lazaro Maza", curso.docentes().get(1).nombreCompleto());
    }

    @Test
    void filtrosMapeaTiposYCategoriasActivos() {
        TipoCurso diplomado = new TipoCurso();
        diplomado.setCodigo("DIPLOMADO");
        diplomado.setNombre("Diplomado");
        diplomado.setActivo(true);
        diplomado.setOrden(10);
        when(tipoCursoRepositorio.findByActivoTrueOrderByOrdenAscNombreAsc()).thenReturn(List.of(diplomado));

        CategoriaTematica registral = new CategoriaTematica();
        registral.setCodigo("DERECHO_REGISTRAL");
        registral.setNombre("Derecho Registral");
        registral.setActivo(true);
        registral.setOrden(10);
        when(categoriaTematicaRepositorio.findByActivoTrueOrderByOrdenAscNombreAsc())
                .thenReturn(List.of(registral));

        FiltrosCursoRespuesta filtros = servicio.filtros();

        assertEquals(1, filtros.tipos().size());
        assertEquals("DIPLOMADO", filtros.tipos().get(0).codigo());
        assertEquals(1, filtros.categorias().size());
        assertEquals("DERECHO_REGISTRAL", filtros.categorias().get(0).codigo());
    }
}
