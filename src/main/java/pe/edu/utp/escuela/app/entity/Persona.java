package pe.edu.utp.escuela.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "persona")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "persona_id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 80)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 80)
    private String apellidoMaterno;

    @Column(name = "foto_url", columnDefinition = "text")
    private String fotoUrl;

    @Column(name = "cargo_profesional", length = 180)
    private String cargoProfesional;

    @Column(nullable = false)
    private boolean activo;

    public String nombreCompleto() {
        StringBuilder completo = new StringBuilder(nombres).append(' ').append(apellidoPaterno);
        if (apellidoMaterno != null && !apellidoMaterno.isBlank()) {
            completo.append(' ').append(apellidoMaterno);
        }
        return completo.toString();
    }
}
