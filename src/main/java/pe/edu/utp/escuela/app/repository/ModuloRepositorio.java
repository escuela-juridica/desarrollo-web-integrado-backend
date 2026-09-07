package pe.edu.utp.escuela.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.Modulo;

public interface ModuloRepositorio extends JpaRepository<Modulo, Long> {

    List<Modulo> findByCursoIdAndActivoTrueOrderByOrdenAsc(Long cursoId);
}
