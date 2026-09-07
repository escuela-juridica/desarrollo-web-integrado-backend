package pe.edu.utp.escuela.app.dto;

public record DocenteFichaFila(
        Long personaId,
        String nombres,
        String apellidoPaterno,
        String apellidoMaterno,
        String fotoUrl,
        String cargoProfesional,
        String biografiaProfesional,
        Integer orden) {
}
