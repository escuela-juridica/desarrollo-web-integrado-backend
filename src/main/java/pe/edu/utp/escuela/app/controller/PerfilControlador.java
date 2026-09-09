package pe.edu.utp.escuela.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.escuela.app.dto.ActualizarPerfilPeticion;
import pe.edu.utp.escuela.app.dto.NuevaContrasenaPerfilPeticion;
import pe.edu.utp.escuela.app.dto.PerfilRespuesta;
import pe.edu.utp.escuela.app.service.PerfilServicio;

@RestController
@RequestMapping("/api/perfil")
public class PerfilControlador {

    private final PerfilServicio perfilServicio;

    public PerfilControlador(PerfilServicio perfilServicio) {
        this.perfilServicio = perfilServicio;
    }

    @GetMapping
    public ResponseEntity<PerfilRespuesta> obtenerPerfil() {
        return ResponseEntity.ok(perfilServicio.obtenerPerfil());
    }

    @PutMapping
    public ResponseEntity<Void> actualizarPerfil(@Valid @RequestBody ActualizarPerfilPeticion peticion) {
        perfilServicio.actualizarPerfil(peticion);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/contrasena")
    public ResponseEntity<Void> asignarContrasena(@Valid @RequestBody NuevaContrasenaPerfilPeticion peticion) {
        perfilServicio.asignarContrasena(peticion);
        return ResponseEntity.noContent().build();
    }
}