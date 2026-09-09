package pe.edu.utp.escuela.app.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    // Métodos del compañero de equipo (no modificar)
    boolean existsByCorreoIgnoreCase(String correo);

    @EntityGraph(attributePaths = "persona")
    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByGoogleSubject(String subject);

    // Método añadido para HU-005 (trae la relación con Persona cargada)
    @EntityGraph(attributePaths = "persona")
    Optional<Usuario> findByIdAndActivoTrue(Long id);
}