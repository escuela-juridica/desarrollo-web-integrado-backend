package pe.edu.utp.escuela.app.repository;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.escuela.app.dto.CursoTarjetaFila;
import pe.edu.utp.escuela.app.entity.Curso;

public interface CursoRepositorio extends JpaRepository<Curso, Long> {

    @Query(value = """
            select new pe.edu.utp.escuela.app.dto.CursoTarjetaFila(
                c.id, c.urlAmigable, c.titulo, c.descripcion, c.imagenPortadaUrl,
                c.modalidad, c.tipoVenta, c.destacado, c.precioRegular,
                c.precioPromocional, c.promocionInicioEn, c.promocionFinEn,
                c.fechaInicio, c.fechaFin, c.fechaCierreMatricula, c.cupoMaximo,
                c.horasAcademicas, t.codigo, t.nombre, ca.codigo, ca.nombre, e.codigo)
            from Curso c
            left join c.tipoCurso t
            left join c.categoriaTematica ca
            join c.estadoCurso e
            where c.publicadoEn is not null
              and e.codigo not in ('BORRADOR', 'CANCELADO')
              and (:texto = ''
                   or lower(c.titulo) like concat('%', :texto, '%')
                   or lower(coalesce(c.descripcion, '')) like concat('%', :texto, '%'))
              and (:tipo = '' or t.codigo = :tipo)
              and (:categoria = '' or ca.codigo = :categoria)
            order by case when c.destacado = true then 0 else 1 end,
                     case when c.fechaInicio is null then 0
                          when c.fechaInicio >= :hoy then 1
                          else 2 end,
                     c.fechaInicio asc,
                     c.id asc
            """,
            countQuery = """
            select count(c.id)
            from Curso c
            left join c.tipoCurso t
            left join c.categoriaTematica ca
            join c.estadoCurso e
            where c.publicadoEn is not null
              and e.codigo not in ('BORRADOR', 'CANCELADO')
              and (:texto = ''
                   or lower(c.titulo) like concat('%', :texto, '%')
                   or lower(coalesce(c.descripcion, '')) like concat('%', :texto, '%'))
              and (:tipo = '' or t.codigo = :tipo)
              and (:categoria = '' or ca.codigo = :categoria)
            """)
    Page<CursoTarjetaFila> buscarPublicados(
            @Param("texto") String texto,
            @Param("tipo") String tipo,
            @Param("categoria") String categoria,
            @Param("hoy") LocalDate hoy,
            Pageable pageable);
}
