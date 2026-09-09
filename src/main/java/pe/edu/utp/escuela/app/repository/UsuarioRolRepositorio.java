package pe.edu.utp.escuela.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.escuela.app.entity.UsuarioRol;

public interface UsuarioRolRepositorio extends JpaRepository<UsuarioRol, UsuarioRol.Clave> {

    @Query("""
            select r.codigo
            from UsuarioRol ur, Rol r
            where ur.id.usuarioId = :usuarioId
              and ur.id.rolId = r.id
              and ur.principal = true
              and r.activo = true
            """)
    List<String> buscarCodigosRolesPrincipales(@Param("usuarioId") Long usuarioId);
}
