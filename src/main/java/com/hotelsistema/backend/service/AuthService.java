package com.hotelsistema.backend.service;

import com.hotelsistema.backend.dto.authDTO.AuthResponse;
import com.hotelsistema.backend.dto.authDTO.LoginRequest;
import com.hotelsistema.backend.dto.authDTO.RegisterRequest;
import com.hotelsistema.backend.dto.usuarioDTO.UsuarioResponse;
import com.hotelsistema.backend.exception.EmailYaRegistradoException;
import com.hotelsistema.backend.model.Rol;
import com.hotelsistema.backend.model.Usuario;
import com.hotelsistema.backend.repository.UsuarioRepository;
import com.hotelsistema.backend.security.JwtService;
import com.hotelsistema.backend.security.UserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .apellidos(request.apellidos())
                .email(request.email())
                .telefono(request.telefono())
                .rol(Rol.CLIENTE) // el registro publico SIEMPRE crea clientes
                .googleAuth(false)
                .passwordHash(passwordEncoder.encode(request.password()))
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        UserPrincipal principal = new UserPrincipal(usuario);
        String token = jwtService.generateToken(principal);

        return AuthResponse.of(token, UsuarioResponse.fromEntity(usuario));
    }

    public AuthResponse login(LoginRequest request) {
        // Si el email no existe o la contrasena no coincide, lanza BadCredentialsException
        // (lo captura el GlobalExceptionHandler y responde 401 sin dar detalles).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(); // no deberia pasar si authenticate() ya paso

        UserPrincipal principal = new UserPrincipal(usuario);
        String token = jwtService.generateToken(principal);

        return AuthResponse.of(token, UsuarioResponse.fromEntity(usuario));
    }
}
