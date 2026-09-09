package pe.edu.utp.escuela.app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.escuela.app.dto.ActualizarPerfilPeticion;
import pe.edu.utp.escuela.app.dto.NuevaContrasenaPerfilPeticion;
import pe.edu.utp.escuela.app.dto.PerfilRespuesta;
import pe.edu.utp.escuela.app.entity.Persona;
import pe.edu.utp.escuela.app.entity.Usuario;
import pe.edu.utp.escuela.app.exception.BusinessValidationException;
import pe.edu.utp.escuela.app.exception.ResourceNotFoundException;
import pe.edu.utp.escuela.app.repository.UsuarioRepositorio;
import pe.edu.utp.escuela.app.security.CurrentUserService;

@Service
public class PerfilServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordPolicyService passwordPolicyService;

    public PerfilServicio(UsuarioRepositorio usuarioRepositorio, 
                          CurrentUserService currentUserService, 
                          PasswordEncoder passwordEncoder,
                          PasswordPolicyService passwordPolicyService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
        this.passwordPolicyService = passwordPolicyService;
    }

    @Transactional(readOnly = true)
    public PerfilRespuesta obtenerPerfil() {
        Long usuarioId = currentUserService.get().userId();
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Persona persona = usuario.getPersona();
        boolean accesoGoogle = usuario.getGoogleSubject() != null;
        boolean puedeCrearContrasena = accesoGoogle && (usuario.getContrasenaHash() == null);

        return new PerfilRespuesta(
                persona.getNombres(),
                persona.getApellidoPaterno(),
                persona.getApellidoMaterno(),
                usuario.getCorreo(),
                persona.getTelefono(),
                persona.getDocumentoIdentidad(),
                persona.getFotoUrl(),
                accesoGoogle,
                puedeCrearContrasena
        );
    }

    @Transactional
    public void actualizarPerfil(ActualizarPerfilPeticion peticion) {
        Long usuarioId = currentUserService.get().userId();
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Persona persona = usuario.getPersona();
        persona.setNombres(peticion.nombres());
        persona.setApellidoPaterno(peticion.apellidoPaterno());
        persona.setApellidoMaterno(peticion.apellidoMaterno());
        persona.setTelefono(peticion.telefono());
        persona.setDocumentoIdentidad(peticion.documentoIdentidad());
    }

    @Transactional
    public void asignarContrasena(NuevaContrasenaPerfilPeticion peticion) {
        if (!peticion.contrasena().equals(peticion.confirmacion())) {
            throw new BusinessValidationException("Las contraseñas no coinciden");
        }

        passwordPolicyService.validate(peticion.contrasena());

        Long usuarioId = currentUserService.get().userId();
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        usuario.setContrasenaHash(passwordEncoder.encode(peticion.contrasena()));
    }
}