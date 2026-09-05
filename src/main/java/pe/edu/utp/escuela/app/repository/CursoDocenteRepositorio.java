package pe.edu.utp.escuela.app.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.escuela.app.dto.DocenteCursoFila;
import pe.edu.utp.escuela.app.entity.CursoDocente;
import pe.edu.utp.escuela.app.entity.CursoDocenteId;

public interface CursoDocenteRepositorio extends JpaRepository<CursoDocente, CursoDocenteId> {

    @Query("""
            select new pe.edu.utp.escuela.app.dto.DocenteCursoFila(
                cd.curso.id, p.id, p.nombres, p.apellidoPaterno,
                p.apellidoMaterno, p.fotoUrl, p.cargoProfesional, cd.orden)
            from CursoDocente cd
            join cd.persona p
            where cd.curso.id in :cursoIds and p.activo = true
            order by cd.curso.id, cd.orden
            """)
    List<DocenteCursoFila> buscarDocentesDeCursos(@Param("cursoIds") Collection<Long> cursoIds);
}
