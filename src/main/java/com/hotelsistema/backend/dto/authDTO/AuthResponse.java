package com.hotelsistema.backend.dto.authDTO;

import com.hotelsistema.backend.dto.usuarioDTO.UsuarioResponse;

public record AuthResponse(
        String token,
        String tipo,
        UsuarioResponse usuario
) {
    public static AuthResponse of(String token, UsuarioResponse usuario) {
        return new AuthResponse(token, "Bearer", usuario);
    }
}
