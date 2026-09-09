package pe.edu.utp.escuela.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.escuela.app.dto.LoginPeticion;
import pe.edu.utp.escuela.app.dto.LoginRespuesta;
import pe.edu.utp.escuela.app.service.AuthServicio;
import pe.edu.utp.escuela.app.security.SessionCookieService;

@RestController
@RequestMapping("/api/auth")
public class AuthControlador {

    private final AuthServicio authServicio;
    private final SessionCookieService sessionCookieService;

    public AuthControlador(AuthServicio authServicio, SessionCookieService sessionCookieService) {
        this.authServicio = authServicio;
        this.sessionCookieService = sessionCookieService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginRespuesta> login(@Valid @RequestBody LoginPeticion peticion) {
        LoginRespuesta respuesta = authServicio.autenticar(peticion);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, sessionCookieService.create(respuesta.token()).toString())
                .body(respuesta);
    }
}