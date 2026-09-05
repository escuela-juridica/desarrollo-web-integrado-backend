package pe.edu.utp.escuela.app.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.escuela.app.entity.Usuario;
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    boolean existsByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    Optional<Usuario> findByGoogleSubject(String subject);
}

