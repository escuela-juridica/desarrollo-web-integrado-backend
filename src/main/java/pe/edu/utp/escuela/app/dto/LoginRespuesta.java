package pe.edu.utp.escuela.app.dto;

public record LoginRespuesta(
    String token,
    String nombreCompleto,
    String correo,
    String fotoUrl
) {}