package pe.edu.utp.escuela.app.dto;

public record DocenteFichaRespuesta(
        Long personaId,
        String nombreCompleto,
        String fotoUrl,
        String cargoProfesional,
        String biografiaProfesional) {
}
