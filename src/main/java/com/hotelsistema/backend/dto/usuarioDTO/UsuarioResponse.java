package com.hotelsistema.backend.dto.usuarioDTO;

import com.hotelsistema.backend.model.Rol;
import com.hotelsistema.backend.model.Usuario;

/**
 * Nunca devolvemos la entidad Usuario directamente en una respuesta HTTP
 * (expondria password_hash). Este DTO es lo unico que sale al cliente.
 */
public record UsuarioResponse(
        Integer id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        Rol rol
) {
    public static UsuarioResponse fromEntity(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol()
        );
    }
}
