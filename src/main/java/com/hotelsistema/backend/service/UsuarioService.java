package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.authDTO.CrearStaffRequest;
import com.hotelsistema.backend.dto.authDTO.RegisterRequest; // <-- Importar este DTO
import com.hotelsistema.backend.dto.usuarioDTO.UsuarioResponse;
import com.hotelsistema.backend.exception.EmailYaRegistradoException;
import com.hotelsistema.backend.exception.RecursoNoEncontradoException;
import com.hotelsistema.backend.exception.RolInvalidoException;
import com.hotelsistema.backend.model.Rol;
import com.hotelsistema.backend.model.Usuario;
import com.hotelsistema.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // Roles permitidos para crearse desde el panel de administracion.
    private static final Set<Rol> ROLES_STAFF = Set.of(Rol.ADMINISTRADOR, Rol.RECEPCIONISTA);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse crearStaff(CrearStaffRequest request) {
        if (!ROLES_STAFF.contains(request.rol())) {
            throw new RolInvalidoException(
                    "Este endpoint solo crea cuentas de ADMINISTRADOR o RECEPCIONISTA. " +
                    "Para crear un CLIENTE usa el registro publico (/api/auth/register)."
            );
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .email(request.email())
                .telefono(request.telefono())
                .rol(request.rol())
                .googleAuth(false)
                .passwordHash(passwordEncoder.encode(request.password()))
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    // --- NUEVO MÉTODO PARA RECEPCIÓN ---
    public UsuarioResponse crearClientePresencial(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .email(request.email())
                .telefono(request.telefono())
                .rol(Rol.CLIENTE) // <-- Forzamos siempre a que sea CLIENTE
                .googleAuth(false)
                .passwordHash(passwordEncoder.encode(request.password()))
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public UsuarioResponse obtenerPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario con ID: " + id));
        
        return UsuarioResponse.fromEntity(usuario);
    }
}