package pe.edu.utp.escuela.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.TipoCurso;

public interface TipoCursoRepositorio extends JpaRepository<TipoCurso, Long> {

    List<TipoCurso> findByActivoTrueOrderByOrdenAscNombreAsc();
}
