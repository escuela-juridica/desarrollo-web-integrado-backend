package pe.edu.utp.escuela.app.dto;

public record DocenteBreveRespuesta(
        Long personaId,
        String nombreCompleto,
        String iniciales,
        String fotoUrl,
        String cargoProfesional) {
}
