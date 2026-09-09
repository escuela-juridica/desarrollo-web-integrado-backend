package pe.edu.utp.escuela.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import pe.edu.utp.escuela.app.dto.LoginPeticion;
import pe.edu.utp.escuela.app.dto.LoginRespuesta;
import pe.edu.utp.escuela.app.entity.Usuario;
import pe.edu.utp.escuela.app.exception.InvalidCredentialsException;
import pe.edu.utp.escuela.app.repository.UsuarioRepositorio;
import pe.edu.utp.escuela.app.security.JwtService;

@Service
public class AuthServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServicio(UsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginRespuesta autenticar(LoginPeticion peticion) {
        Usuario usuario = usuarioRepositorio.findByCorreoIgnoreCase(peticion.correo())
                .orElseThrow(InvalidCredentialsException::new);

        if (!usuario.isActivo() || usuario.getContrasenaHash() == null) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(peticion.contrasena(), usuario.getContrasenaHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.issue(usuario.getId(), usuario.getCorreo(), List.of("ALUMNO"));
        String nombreCompleto = usuario.getPersona().nombreCompleto();
        String fotoUrl = usuario.getPersona().getFotoUrl();

        return new LoginRespuesta(token, nombreCompleto, usuario.getCorreo(), fotoUrl);
    }
}