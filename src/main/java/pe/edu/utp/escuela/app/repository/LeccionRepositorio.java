package pe.edu.utp.escuela.app.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.escuela.app.entity.Leccion;

public interface LeccionRepositorio extends JpaRepository<Leccion, Long> {

    @Query("""
            select l from Leccion l
            where l.modulo.id in :moduloIds and l.activo = true
            order by l.modulo.id, l.orden
            """)
    List<Leccion> buscarActivasDeModulos(@Param("moduloIds") Collection<Long> moduloIds);
}
