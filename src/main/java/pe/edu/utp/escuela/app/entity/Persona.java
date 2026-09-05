package pe.edu.utp.escuela.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "persona") @Getter @Setter
public class Persona extends RegistroAuditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Long id;
    @Column(nullable = false, length = 120)
    private String nombres;
    @Column(name = "apellido_paterno", nullable = false, length = 80)
    private String apellidoPaterno;
    @Column(name = "apellido_materno", length = 80)
    private String apellidoMaterno;
    @Column(length = 30)
    private String telefono;
    @Column(name = "documento_identidad", length = 30, unique = true)
    private String documentoIdentidad;
    @Column(name = "foto_url", columnDefinition = "text")
    private String fotoUrl;
    @Column(nullable = false)
    private boolean activo = true;
}

