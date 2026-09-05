package pe.edu.utp.escuela.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.CategoriaTematica;

public interface CategoriaTematicaRepositorio extends JpaRepository<CategoriaTematica, Long> {

    List<CategoriaTematica> findByActivoTrueOrderByOrdenAscNombreAsc();
}
