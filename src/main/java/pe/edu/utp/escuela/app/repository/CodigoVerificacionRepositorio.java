package pe.edu.utp.escuela.app.repository;
import java.time.Instant;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.escuela.app.entity.CodigoVerificacionCorreo;
public interface CodigoVerificacionRepositorio extends JpaRepository<CodigoVerificacionCorreo, Long> {
    @Modifying
    @Query("update CodigoVerificacionCorreo c set c.invalidadoEn = :ahora, c.modificadoEn = :ahora where c.usuario.id = :id and c.utilizadoEn is null and c.invalidadoEn is null")
    void invalidarAnteriores(@Param("id") Long usuarioId, @Param("ahora") Instant ahora);
}

