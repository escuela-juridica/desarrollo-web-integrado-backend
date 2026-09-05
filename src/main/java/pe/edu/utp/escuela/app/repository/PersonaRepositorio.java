package pe.edu.utp.escuela.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.Persona;
public interface PersonaRepositorio extends JpaRepository<Persona, Long> {
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}

