package pe.edu.utp.escuela.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.utp.escuela.app.dto.DocenteFichaFila;
import pe.edu.utp.escuela.app.dto.DuracionLeccionFila;
import pe.edu.utp.escuela.app.dto.FichaCursoRespuesta;
import pe.edu.utp.escuela.app.dto.ModuloFichaRespuesta;
import pe.edu.utp.escuela.app.dto.RecursoVistaPreviaFila;
import pe.edu.utp.escuela.app.dto.VistaPreviaRespuesta;
import pe.edu.utp.escuela.app.entity.CategoriaTematica;
import pe.edu.utp.escuela.app.entity.Curso;
import pe.edu.utp.escuela.app.entity.EstadoCurso;
import pe.edu.utp.escuela.app.entity.Leccion;
import pe.edu.utp.escuela.app.entity.Modulo;
import pe.edu.utp.escuela.app.entity.TipoCurso;
import pe.edu.utp.escuela.app.exception.ResourceNotFoundException;
import pe.edu.utp.escuela.app.repository.CursoDocenteRepositorio;
import pe.edu.utp.escuela.app.repository.CursoRepositorio;
import pe.edu.utp.escuela.app.repository.LeccionRepositorio;
import pe.edu.utp.escuela.app.repository.MaterialLeccionRepositorio;
import pe.edu.utp.escuela.app.repository.MatriculaRepositorio;
import pe.edu.utp.escuela.app.repository.ModuloRepositorio;

@ExtendWith(MockitoExtension.class)
class FichaCursoServicioTests {

    @Mock private CursoRepositorio cursoRepositorio;
    @Mock private MatriculaRepositorio matriculaRepositorio;
    @Mock private CursoDocenteRepositorio cursoDocenteRepositorio;
    @Mock private ModuloRepositorio moduloRepositorio;
    @Mock private LeccionRepositorio leccionRepositorio;
    @Mock private MaterialLeccionRepositorio materialLeccionRepositorio;

    private FichaCursoServicio servicio;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-07T12:00:00Z"), ZoneId.of("America/Lima"));
        servicio = new FichaCursoServicio(
                cursoRepositorio,
                matriculaRepositorio,
                cursoDocenteRepositorio,
                moduloRepositorio,
                leccionRepositorio,
                materialLeccionRepositorio,
                new CourseCommercialStatusService(clock));
    }

    private Curso curso(String modalidad, LocalDate fechaInicio, LocalDate fechaFin) {
        EstadoCurso estado = new EstadoCurso();
        estado.setCodigo("PUBLICADO");

        TipoCurso tipo = new TipoCurso();
        tipo.setCodigo("DIPLOMADO");
        tipo.setNombre("Diplomado");

        CategoriaTematica categoria = new CategoriaTematica();
        categoria.setCodigo("DERECHO_REGISTRAL");
        categoria.setNombre("Derecho Registral");

        Curso curso = new Curso();
        curso.setId(1L);
        curso.setUrlAmigable("registral");
        curso.setTitulo("Diplomado en Derecho Registral");
        curso.setDescripcion("Descripción del curso");
        curso.setModalidad(modalidad);
        curso.setTipoVenta("GRATUITO");
        curso.setEstadoCurso(estado);
        curso.setTipoCurso(tipo);
        curso.setCategoriaTematica(categoria);
        curso.setPrecioRegular(BigDecimal.ZERO);
        curso.setFechaInicio(fechaInicio);
        curso.setFechaFin(fechaFin);
        curso.setHorasAcademicas(new BigDecimal("120.00"));
        curso.setBeneficios(new String[] {"Acceso inmediato"});
        curso.setPublicadoEn(Instant.parse("2026-08-01T00:00:00Z"));
        return curso;
    }

    private Modulo modulo(Long id, Curso curso, String titulo, int orden) {
        Modulo modulo = new Modulo();
        modulo.setId(id);
        modulo.setCurso(curso);
        modulo.setTitulo(titulo);
        modulo.setOrden(orden);
        modulo.setActivo(true);
        return modulo;
    }

    private Leccion leccion(Long id, Modulo modulo, String titulo, int orden, boolean vistaPrevia) {
        Leccion leccion = new Leccion();
        leccion.setId(id);
        leccion.setModulo(modulo);
        leccion.setTitulo(titulo);
        leccion.setOrden(orden);
        leccion.setTipo("GRABADA");
        leccion.setEstado("DISPONIBLE");
        leccion.setEsVistaPrevia(vistaPrevia);
        leccion.setActivo(true);
        return leccion;
    }

    private DuracionLeccionFila duracion(Long leccionId, Integer segundos) {
        return new DuracionLeccionFila() {
            @Override
            public Long getLeccionId() {
                return leccionId;
            }

            @Override
            public Integer getDuracionSegundos() {
                return segundos;
            }
        };
    }

    @Test
    void obtenerLanzaNoEncontradoCuandoElCursoNoExiste() {
        when(cursoRepositorio.buscarFichaPublica("no-existe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> servicio.obtener("no-existe"));
    }

    @Test
    void obtenerOcultaFechaFinParaVirtualAunqueLaEntidadLaTenga() {
        Curso curso = curso("VIRTUAL", null, LocalDate.of(2026, 12, 1));
        when(cursoRepositorio.buscarFichaPublica("registral")).thenReturn(Optional.of(curso));
        when(matriculaRepositorio.contarActivas(any())).thenReturn(Map.of());
        when(cursoDocenteRepositorio.buscarDocentesDelCurso(1L)).thenReturn(List.of());
        when(moduloRepositorio.findByCursoIdAndActivoTrueOrderByOrdenAsc(1L)).thenReturn(List.of());

        FichaCursoRespuesta ficha = servicio.obtener("registral");

        assertNull(ficha.fechaFin());
        assertEquals("IMMEDIATE_START", ficha.estadoComercial().codigo());
    }

    @Test
    void obtenerConservaFechaFinParaModalidadHibrida() {
        Curso curso = curso("HIBRIDO", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 12, 1));
        when(cursoRepositorio.buscarFichaPublica("registral")).thenReturn(Optional.of(curso));
        when(matriculaRepositorio.contarActivas(any())).thenReturn(Map.of());
        when(cursoDocenteRepositorio.buscarDocentesDelCurso(1L)).thenReturn(List.of());
        when(moduloRepositorio.findByCursoIdAndActivoTrueOrderByOrdenAsc(1L)).thenReturn(List.of());

        FichaCursoRespuesta ficha = servicio.obtener("registral");

        assertEquals(LocalDate.of(2026, 12, 1), ficha.fechaFin());
    }

    @Test
    void obtenerAgrupaLeccionesPorModuloYSoloInformaDuracionCuandoExiste() {
        Curso curso = curso("VIRTUAL", null, null);
        Modulo modulo1 = modulo(10L, curso, "Módulo 1", 1);
        Modulo modulo2 = modulo(11L, curso, "Módulo 2", 2);
        Leccion leccion1 = leccion(100L, modulo1, "Bienvenida", 1, true);
        Leccion leccion2 = leccion(101L, modulo1, "Lección 2", 2, false);
        Leccion leccion3 = leccion(102L, modulo2, "Lección 3", 1, false);

        when(cursoRepositorio.buscarFichaPublica("registral")).thenReturn(Optional.of(curso));
        when(matriculaRepositorio.contarActivas(any())).thenReturn(Map.of());
        when(cursoDocenteRepositorio.buscarDocentesDelCurso(1L)).thenReturn(List.of());
        when(moduloRepositorio.findByCursoIdAndActivoTrueOrderByOrdenAsc(1L))
                .thenReturn(List.of(modulo1, modulo2));
        when(leccionRepositorio.buscarActivasDeModulos(List.of(10L, 11L)))
                .thenReturn(List.of(leccion1, leccion2, leccion3));
        when(materialLeccionRepositorio.buscarDuraciones(List.of(100L, 101L, 102L)))
                .thenReturn(List.of(duracion(100L, 720), duracion(101L, null)));

        FichaCursoRespuesta ficha = servicio.obtener("registral");

        assertEquals(2, ficha.modulos().size());
        ModuloFichaRespuesta primerModulo = ficha.modulos().get(0);
        assertEquals(2, primerModulo.lecciones().size());
        assertEquals(720, primerModulo.lecciones().get(0).duracionSegundos());
        assertNull(primerModulo.lecciones().get(1).duracionSegundos());
        assertNull(ficha.modulos().get(1).lecciones().get(0).duracionSegundos());
    }

    @Test
    void obtenerOrdenaDocentesPorOrdenYComponeNombreCompleto() {
        Curso curso = curso("VIRTUAL", null, null);
        when(cursoRepositorio.buscarFichaPublica("registral")).thenReturn(Optional.of(curso));
        when(matriculaRepositorio.contarActivas(any())).thenReturn(Map.of());
        when(moduloRepositorio.findByCursoIdAndActivoTrueOrderByOrdenAsc(1L)).thenReturn(List.of());
        when(cursoDocenteRepositorio.buscarDocentesDelCurso(1L)).thenReturn(List.of(
                new DocenteFichaFila(2L, "Ariana", "Lazaro", "Maza", null, "Docente", "Bio A", 2),
                new DocenteFichaFila(1L, "Miguel", "Saldivar", "Davalos", null, "Docente", "Bio M", 1)));

        FichaCursoRespuesta ficha = servicio.obtener("registral");

        assertEquals(2, ficha.docentes().size());
        assertEquals("Miguel Saldivar Davalos", ficha.docentes().get(0).nombreCompleto());
        assertEquals("Ariana Lazaro Maza", ficha.docentes().get(1).nombreCompleto());
    }

    @Test
    void obtenerVistaPreviaLanzaNoEncontradoCuandoNoHayMateriales() {
        when(materialLeccionRepositorio.buscarVistaPrevia("registral", 999L)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> servicio.obtenerVistaPrevia("registral", 999L));
    }

    @Test
    void obtenerVistaPreviaMapeaMaterialesAutorizados() {
        when(materialLeccionRepositorio.buscarVistaPrevia("registral", 100L)).thenReturn(List.of(
                new RecursoVistaPreviaFila(100L, 13L, "Video de vista previa", 1, "VIDEO",
                        "ARCHIVO_LOCAL", "cursos/registral/vista-previa.mp4",
                        "vista-previa-registral.mp4", "video/mp4", 720, false)));

        VistaPreviaRespuesta respuesta = servicio.obtenerVistaPrevia("registral", 100L);

        assertEquals(100L, respuesta.leccionId());
        assertEquals(1, respuesta.materiales().size());
        assertEquals("cursos/registral/vista-previa.mp4", respuesta.materiales().get(0).referencia());
    }
}
